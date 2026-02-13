package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.constants.TrangThaiHoaDon;
import com.example.datn_sevenstrike.dto.request.HoaDonRequest;
import com.example.datn_sevenstrike.dto.response.HoaDonChiTietResponse;
import com.example.datn_sevenstrike.dto.response.HoaDonResponse;
import com.example.datn_sevenstrike.entity.*;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HoaDonService {

    private final HoaDonRepository repo;
    private final LichSuHoaDonRepository lichSuRepo;
    private final ModelMapper mapper;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;

    // POS deps
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository;
    private final GiaoDichThanhToanRepository giaoDichThanhToanRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    public List<HoaDonResponse> all() {
        return repo.getDanhSachHoaDon();
    }

    public Page<HoaDonResponse> page(int pageNo, int pageSize) {
        int p = Math.max(pageNo, 0);
        int s = Math.max(pageSize, 1);
        var pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "id"));
        return repo.findAllByXoaMemFalse(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public HoaDonResponse one(Integer id) {
        HoaDon hd = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy hóa đơn id=" + id));

        HoaDonResponse res = toResponse(hd);

        // ✅ lấy chi tiết "alive" để hiển thị
        List<HoaDonChiTiet> listCT = hoaDonChiTietRepository.findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(id);

        List<HoaDonChiTietResponse> chiTietList = listCT.stream()
                .map(ct -> {
                    ChiTietSanPham ctsp = ct.getChiTietSanPham();
                    SanPham sp = (ctsp == null) ? null : ctsp.getSanPham();

                    String mauSac = null;
                    String kichCo = null;
                    String loaiSan = null;
                    String formChan = null;

                    if (ctsp != null) {
                        MauSac ms = ctsp.getMauSac();
                        if (ms != null) {
                            mauSac = ms.getTenMauSac();
                        }

                        KichThuoc kt = ctsp.getKichThuoc();
                        if (kt != null) {
                            if (kt.getGiaTriKichThuoc() != null) {
                                kichCo = String.valueOf(kt.getGiaTriKichThuoc());
                            } else {
                                kichCo = kt.getTenKichThuoc();
                            }
                        }

                        LoaiSan ls = ctsp.getLoaiSan();
                        if (ls != null) {
                            loaiSan = ls.getTenLoaiSan();
                        }

                        FormChan fc = ctsp.getFormChan();
                        if (fc != null) {
                            formChan = fc.getTenFormChan();
                        }
                    }

                    BigDecimal donGia = ct.getDonGia() == null ? BigDecimal.ZERO : ct.getDonGia();
                    int soLuong = ct.getSoLuong() == null ? 0 : ct.getSoLuong();

                    return HoaDonChiTietResponse.builder()
                            .id(ct.getId())
                            .idHoaDon(ct.getIdHoaDon())
                            .idChiTietSanPham(ct.getIdChiTietSanPham())
                            .maHoaDonChiTiet(ct.getMaHoaDonChiTiet())
                            .soLuong(soLuong)
                            .donGia(donGia)
                            .thanhTien(donGia.multiply(BigDecimal.valueOf(soLuong)))

                            .ghiChu(ct.getGhiChu())
                            .xoaMem(ct.getXoaMem())

                            .maHoaDon(hd.getMaHoaDon())

                            .maChiTietSanPham(ctsp == null ? null : ctsp.getMaChiTietSanPham())
                            .maSanPham(sp == null ? null : sp.getMaSanPham())
                            .tenSanPham(sp == null ? null : sp.getTenSanPham())

                            .mauSac(mauSac)
                            .kichCo(kichCo)
                            .loaiSan(loaiSan)
                            .formChan(formChan)

                            .duongDanAnhDaiDien(null)
                            .build();
                })
                .toList();

        res.setChiTietHoaDon(chiTietList);
        return res;
    }

    /**
     * TẠO HÓA ĐƠN:
     * - create chỉ tạo đơn "chờ xác nhận" để POS làm tiếp
     */
    @Transactional
    public HoaDonResponse create(HoaDonRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        HoaDon hd = mapper.map(req, HoaDon.class);
        hd.setId(null);

        applyDefaults(hd);

        if (hd.getTrangThaiHienTai() == null) {
            hd.setTrangThaiHienTai(TrangThaiHoaDon.CHO_XAC_NHAN.code);
        }

        validateTheoLoaiDon(hd);

        HoaDon saved = repo.save(hd);
        pushHistory(saved.getId(), saved.getTrangThaiHienTai(), "Tạo đơn");
        return toResponse(saved);
    }

    /**
     * POS: CHỐT ĐƠN TẠI QUẦY - TIỀN MẶT
     */
    @Transactional
    public HoaDonResponse confirmTaiQuayTienMat(Integer idHoaDon, String note) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id hóa đơn");

        HoaDon hd = repo.findByIdAndXoaMemFalse(idHoaDon)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDon id=" + idHoaDon));

        if (TrangThaiHoaDon.isTerminal(hd.getTrangThaiHienTai())) {
            return toResponse(hd);
        }

        hd.setLoaiDon(false);
        if (hd.getPhiVanChuyen() == null) hd.setPhiVanChuyen(BigDecimal.ZERO);

        List<HoaDonChiTiet> items = hoaDonChiTietRepository.findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(idHoaDon);
        if (items == null || items.isEmpty()) {
            throw new BadRequestEx("Hóa đơn chưa có sản phẩm, không thể chốt");
        }

        BigDecimal tongTienHang = BigDecimal.ZERO;
        for (HoaDonChiTiet ct : items) {
            if (ct.getSoLuong() == null || ct.getSoLuong() <= 0) {
                throw new BadRequestEx("Số lượng không hợp lệ ở chi tiết hóa đơn id=" + ct.getId());
            }
            if (ct.getDonGia() == null || ct.getDonGia().signum() < 0) {
                throw new BadRequestEx("Đơn giá không hợp lệ ở chi tiết hóa đơn id=" + ct.getId());
            }
            tongTienHang = tongTienHang.add(ct.getDonGia().multiply(BigDecimal.valueOf(ct.getSoLuong())));
        }

        BigDecimal tienGiam = tinhVaConsumeVoucherNeuCo(hd, tongTienHang);

        BigDecimal tongTien = tongTienHang.add(hd.getPhiVanChuyen() == null ? BigDecimal.ZERO : hd.getPhiVanChuyen());
        BigDecimal tongTienSauGiam = tongTien.subtract(tienGiam);
        if (tongTienSauGiam.signum() < 0) tongTienSauGiam = BigDecimal.ZERO;

        hd.setTongTien(tongTien);
        hd.setTongTienSauGiam(tongTienSauGiam);

        for (HoaDonChiTiet ct : items) {
            Integer ctspId = ct.getIdChiTietSanPham();
            Integer qty = ct.getSoLuong();
            if (ctspId == null) {
                throw new BadRequestEx("Thiếu id_chi_tiet_san_pham ở chi tiết hóa đơn id=" + ct.getId());
            }

            int updated = chiTietSanPhamRepository.giamTonNeuDu(ctspId, qty);
            if (updated == 0) {
                ChiTietSanPham ctsp = chiTietSanPhamRepository.findByIdAndXoaMemFalse(ctspId)
                        .orElseThrow(() -> new BadRequestEx("CTSP không tồn tại hoặc đã xóa: id=" + ctspId));

                String ma = ctsp.getMaChiTietSanPham();
                Integer ton = ctsp.getSoLuong();
                throw new BadRequestEx("Không đủ tồn kho cho CTSP " + (ma == null ? ("id=" + ctspId) : ma)
                        + " (tồn hiện tại: " + (ton == null ? 0 : ton) + ")");
            }
        }

        PhuongThucThanhToan pttt = phuongThucThanhToanRepository
                .findFirstByTenPhuongThucThanhToanIgnoreCaseAndTrangThaiTrueAndXoaMemFalse("Tiền mặt")
                .orElseThrow(() -> new BadRequestEx("Chưa cấu hình phương thức thanh toán 'Tiền mặt'"));

        GiaoDichThanhToan gd = new GiaoDichThanhToan();
        gd.setId(null);
        gd.setIdHoaDon(idHoaDon);
        gd.setIdPhuongThucThanhToan(pttt.getId());
        gd.setSoTien(tongTienSauGiam);
        gd.setTrangThai("SUCCESS");
        gd.setThoiGianTao(LocalDateTime.now());
        gd.setXoaMem(false);
        gd.setGhiChu((note == null || note.isBlank()) ? "Chốt đơn tại quầy - tiền mặt" : note.trim());
        giaoDichThanhToanRepository.save(gd);

        hd.setTrangThaiHienTai(TrangThaiHoaDon.HOAN_THANH.code);
        hd.setNgayThanhToan(LocalDateTime.now());
        hd.setNgayCapNhat(LocalDateTime.now());

        validateTheoLoaiDon(hd);

        HoaDon saved = repo.save(hd);

        pushHistory(saved.getId(), TrangThaiHoaDon.HOAN_THANH.code,
                (note == null || note.isBlank()) ? "Chốt đơn tại quầy - tiền mặt" : note.trim());

        return toResponse(saved);
    }

    @Transactional
    public HoaDonResponse changeStatus(Integer idHoaDon, Integer newStatus, String note) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id hóa đơn");
        if (newStatus == null) throw new BadRequestEx("Thiếu trạng thái mới");

        HoaDon hd = repo.findByIdAndXoaMemFalse(idHoaDon)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDon id=" + idHoaDon));

        Integer oldStatus = hd.getTrangThaiHienTai();
        if (oldStatus == null) oldStatus = TrangThaiHoaDon.CHO_XAC_NHAN.code;

        if (TrangThaiHoaDon.isTerminal(oldStatus)) {
            throw new BadRequestEx("Đơn đã kết thúc, không thể đổi trạng thái");
        }

        // CHỐT: chỉ 1..5
        if (newStatus < 1 || newStatus > 5) {
            throw new BadRequestEx("trang_thai_hien_tai không hợp lệ (1..5)");
        }

        if (newStatus < oldStatus) {
            throw new BadRequestEx("Không thể chuyển trạng thái lùi");
        }

        // đơn tại quầy không đi qua trạng thái giao hàng
        if (Boolean.FALSE.equals(hd.getLoaiDon()) && (newStatus == 2 || newStatus == 3 || newStatus == 4)) {
            throw new BadRequestEx("Đơn tại quầy không có trạng thái giao hàng");
        }

        hd.setTrangThaiHienTai(newStatus);

        if (newStatus == TrangThaiHoaDon.HOAN_THANH.code && hd.getNgayThanhToan() == null) {
            hd.setNgayThanhToan(LocalDateTime.now());
        }

        hd.setNgayCapNhat(LocalDateTime.now());
        HoaDon saved = repo.save(hd);

        pushHistory(saved.getId(), newStatus,
                (note == null || note.isBlank()) ? "Cập nhật trạng thái" : note.trim());

        return toResponse(saved);
    }

    @Transactional
    public HoaDonResponse payCashAndComplete(Integer idHoaDon, String note) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id hóa đơn");

        HoaDon hd = repo.findByIdAndXoaMemFalse(idHoaDon)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDon id=" + idHoaDon));

        Integer st = hd.getTrangThaiHienTai();
        if (TrangThaiHoaDon.isTerminal(st)) return toResponse(hd);

        hd.setLoaiDon(false);
        hd.setTrangThaiHienTai(TrangThaiHoaDon.HOAN_THANH.code);
        hd.setNgayThanhToan(LocalDateTime.now());
        hd.setNgayCapNhat(LocalDateTime.now());

        HoaDon saved = repo.save(hd);

        pushHistory(saved.getId(), TrangThaiHoaDon.HOAN_THANH.code,
                (note == null || note.isBlank()) ? "Thanh toán tiền mặt tại quầy" : note.trim());

        return toResponse(saved);
    }

    @Transactional
    public void delete(Integer id) {
        HoaDon hd = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDon id=" + id));
        hd.setXoaMem(true);
        hd.setNgayCapNhat(LocalDateTime.now());
        repo.save(hd);
    }

    // ================== PRIVATE ==================

    private void pushHistory(Integer idHoaDon, Integer trangThai, String ghiChu) {
        LichSuHoaDon h = new LichSuHoaDon();
        h.setId(null);
        h.setIdHoaDon(idHoaDon);
        h.setTrangThai(trangThai);
        h.setGhiChu(ghiChu);
        h.setXoaMem(false);
        lichSuRepo.save(h);
    }

    private void applyDefaults(HoaDon hd) {
        if (hd.getXoaMem() == null) hd.setXoaMem(false);
        if (hd.getNgayTao() == null) hd.setNgayTao(LocalDateTime.now());

        if (hd.getLoaiDon() == null) hd.setLoaiDon(false);
        if (hd.getPhiVanChuyen() == null) hd.setPhiVanChuyen(BigDecimal.ZERO);

        if (hd.getTenKhachHang() != null) hd.setTenKhachHang(hd.getTenKhachHang().trim());
        if (hd.getDiaChiKhachHang() != null) hd.setDiaChiKhachHang(hd.getDiaChiKhachHang().trim());
        if (hd.getSoDienThoaiKhachHang() != null) hd.setSoDienThoaiKhachHang(hd.getSoDienThoaiKhachHang().trim());
        if (hd.getEmailKhachHang() != null) hd.setEmailKhachHang(hd.getEmailKhachHang().trim());
        if (hd.getGhiChu() != null) hd.setGhiChu(hd.getGhiChu().trim());

        if (hd.getTongTien() == null) hd.setTongTien(BigDecimal.ZERO);
        if (hd.getTongTienSauGiam() == null) hd.setTongTienSauGiam(BigDecimal.ZERO);
    }

    private void validateTheoLoaiDon(HoaDon hd) {
        if (hd.getTenKhachHang() == null || hd.getTenKhachHang().isBlank()) {
            throw new BadRequestEx("Thiếu ten_khach_hang");
        }
        if (hd.getSoDienThoaiKhachHang() == null || hd.getSoDienThoaiKhachHang().isBlank()) {
            throw new BadRequestEx("Thiếu so_dien_thoai_khach_hang");
        }

        if (Boolean.TRUE.equals(hd.getLoaiDon())) {
            if (hd.getDiaChiKhachHang() == null || hd.getDiaChiKhachHang().isBlank()) {
                throw new BadRequestEx("Thiếu dia_chi_khach_hang (đơn giao hàng)");
            }
        } else {
            if (hd.getDiaChiKhachHang() == null) hd.setDiaChiKhachHang("");
        }

        if (hd.getTongTien() == null || hd.getTongTien().signum() < 0) {
            throw new BadRequestEx("tong_tien không hợp lệ");
        }
        if (hd.getTongTienSauGiam() == null || hd.getTongTienSauGiam().signum() < 0) {
            throw new BadRequestEx("tong_tien_sau_giam không hợp lệ");
        }
        if (hd.getTongTienSauGiam().compareTo(hd.getTongTien()) > 0) {
            throw new BadRequestEx("tong_tien_sau_giam không được lớn hơn tong_tien");
        }

        if (hd.getTrangThaiHienTai() == null) {
            throw new BadRequestEx("Thiếu trang_thai_hien_tai");
        }

        // CHỐT: 1..5
        if (hd.getTrangThaiHienTai() < 1 || hd.getTrangThaiHienTai() > 5) {
            throw new BadRequestEx("trang_thai_hien_tai không hợp lệ (1..5)");
        }
    }

    private BigDecimal tinhVaConsumeVoucherNeuCo(HoaDon hd, BigDecimal tongTienHang) {
        BigDecimal giam = BigDecimal.ZERO;
        LocalDate today = LocalDate.now();

        if (hd.getIdPhieuGiamGiaCaNhan() != null) {
            if (hd.getIdKhachHang() == null) {
                throw new BadRequestEx("Đơn dùng voucher cá nhân nhưng thiếu id_khach_hang");
            }

            PhieuGiamGiaCaNhan caNhan = phieuGiamGiaCaNhanRepository
                    .findByIdAndXoaMemFalse(hd.getIdPhieuGiamGiaCaNhan())
                    .orElseThrow(() -> new BadRequestEx("Voucher cá nhân không tồn tại hoặc đã xóa"));

            if (!hd.getIdKhachHang().equals(caNhan.getIdKhachHang())) {
                throw new BadRequestEx("Voucher cá nhân không thuộc khách hàng này");
            }
            if (Boolean.TRUE.equals(caNhan.getDaSuDung())) {
                throw new BadRequestEx("Voucher cá nhân đã sử dụng");
            }

            PhieuGiamGia pgg = caNhan.getPhieuGiamGia();
            if (pgg == null) {
                pgg = phieuGiamGiaRepository.findByIdAndXoaMemFalse(caNhan.getIdPhieuGiamGia())
                        .orElseThrow(() -> new BadRequestEx("Phiếu giảm giá gốc không tồn tại"));
            }

            validateVoucher(pgg, tongTienHang, today);

            if (hd.getIdPhieuGiamGia() == null) {
                hd.setIdPhieuGiamGia(pgg.getId());
            } else if (!hd.getIdPhieuGiamGia().equals(pgg.getId())) {
                throw new BadRequestEx("Voucher cá nhân không khớp id_phieu_giam_gia trên hóa đơn");
            }

            giam = tinhSoTienGiam(pgg, tongTienHang);

            int okConsume = phieuGiamGiaRepository.consumeNeuCon(pgg.getId());
            if (okConsume == 0) {
                throw new BadRequestEx("Phiếu giảm giá đã hết lượt sử dụng");
            }

            int okMark = phieuGiamGiaCaNhanRepository.markUsedNeuHopLe(caNhan.getId(), hd.getIdKhachHang());
            if (okMark == 0) {
                throw new BadRequestEx("Không thể đánh dấu dùng voucher cá nhân (có thể đã dùng/không hợp lệ)");
            }

            return giam;
        }

        if (hd.getIdPhieuGiamGia() != null) {
            PhieuGiamGia pgg = phieuGiamGiaRepository.findByIdAndXoaMemFalse(hd.getIdPhieuGiamGia())
                    .orElseThrow(() -> new BadRequestEx("Phiếu giảm giá không tồn tại hoặc đã xóa"));

            validateVoucher(pgg, tongTienHang, today);

            giam = tinhSoTienGiam(pgg, tongTienHang);

            int ok = phieuGiamGiaRepository.consumeNeuCon(pgg.getId());
            if (ok == 0) {
                throw new BadRequestEx("Phiếu giảm giá đã hết lượt sử dụng");
            }
        }

        return giam;
    }

    private void validateVoucher(PhieuGiamGia pgg, BigDecimal tongTienHang, LocalDate today) {
        if (Boolean.TRUE.equals(pgg.getXoaMem()) || !Boolean.TRUE.equals(pgg.getTrangThai())) {
            throw new BadRequestEx("Phiếu giảm giá không hoạt động");
        }
        if (pgg.getNgayBatDau() != null && today.isBefore(pgg.getNgayBatDau())) {
            throw new BadRequestEx("Phiếu giảm giá chưa đến ngày áp dụng");
        }
        if (pgg.getNgayKetThuc() != null && today.isAfter(pgg.getNgayKetThuc())) {
            throw new BadRequestEx("Phiếu giảm giá đã hết hạn");
        }
        if (pgg.getHoaDonToiThieu() != null && tongTienHang.compareTo(pgg.getHoaDonToiThieu()) < 0) {
            throw new BadRequestEx("Chưa đạt hóa đơn tối thiểu để áp dụng phiếu giảm giá");
        }
        if (pgg.getSoLuongSuDung() == null || pgg.getSoLuongSuDung() <= 0) {
            throw new BadRequestEx("Phiếu giảm giá đã hết lượt sử dụng");
        }
    }

    private BigDecimal tinhSoTienGiam(PhieuGiamGia pgg, BigDecimal tongTienHang) {
        BigDecimal giam;

        // false: % | true: tiền
        if (Boolean.FALSE.equals(pgg.getLoaiPhieuGiamGia())) {
            BigDecimal percent = pgg.getGiaTriGiamGia();
            if (percent == null || percent.signum() <= 0) return BigDecimal.ZERO;

            giam = tongTienHang.multiply(percent)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            giam = pgg.getGiaTriGiamGia();
            if (giam == null || giam.signum() <= 0) return BigDecimal.ZERO;
            giam = giam.setScale(2, RoundingMode.HALF_UP);
        }

        if (pgg.getSoTienGiamToiDa() != null && pgg.getSoTienGiamToiDa().signum() > 0) {
            if (giam.compareTo(pgg.getSoTienGiamToiDa()) > 0) giam = pgg.getSoTienGiamToiDa();
        }

        if (giam.compareTo(tongTienHang) > 0) giam = tongTienHang;
        if (giam.signum() < 0) giam = BigDecimal.ZERO;

        return giam;
    }

    private HoaDonResponse toResponse(HoaDon e) {
        return mapper.map(e, HoaDonResponse.class);
    }
}
