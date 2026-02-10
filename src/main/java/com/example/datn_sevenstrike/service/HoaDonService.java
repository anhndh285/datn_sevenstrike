package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.constants.TrangThaiHoaDon;
import com.example.datn_sevenstrike.dto.request.HoaDonRequest;
import com.example.datn_sevenstrike.dto.response.HoaDonChiTietResponse;
import com.example.datn_sevenstrike.dto.response.HoaDonResponse;
import com.example.datn_sevenstrike.entity.*;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    public HoaDonResponse one(Integer id) {

        HoaDon hd = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy hóa đơn id=" + id));

        HoaDonResponse res = toResponse(hd);

        List<HoaDonChiTiet> listCT = hoaDonChiTietRepository.findByIdHoaDon(id);

        List<HoaDonChiTietResponse> chiTietList = listCT.stream()
                .map(ct -> HoaDonChiTietResponse.builder()
                        .id(ct.getId())
                        .idHoaDon(ct.getIdHoaDon())
                        .idChiTietSanPham(ct.getIdChiTietSanPham())
                        .soLuong(ct.getSoLuong())
                        .donGia(ct.getDonGia())
                        .thanhTien(ct.getDonGia().multiply(BigDecimal.valueOf(ct.getSoLuong())))
                        .maSanPham(ct.getChiTietSanPham().getSanPham().getMaSanPham())
                        .tenSanPham(ct.getChiTietSanPham().getSanPham().getTenSanPham())
                        .duongDanAnhDaiDien(null) // nếu cần ảnh thì join ảnh đại diện sau
                        .build()
                ).toList();

        res.setChiTietHoaDon(chiTietList);

        return res;
    }

    /**
     * TẠO HÓA ĐƠN:
     * - KHÔNG tự HOÀN_THÀNH khi tại quầy nữa
     * - create chỉ tạo đơn "chờ xác nhận" để POS làm tiếp (thêm SP, áp voucher, rồi confirm)
     */
    @Transactional
    public HoaDonResponse create(HoaDonRequest req) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        HoaDon hd = mapper.map(req, HoaDon.class);
        hd.setId(null);

        applyDefaults(hd);

        // mặc định trạng thái khi tạo
        if (hd.getTrangThaiHienTai() == null) {
            hd.setTrangThaiHienTai(TrangThaiHoaDon.CHO_XAC_NHAN.code);
        }

        // validate theo loại đơn
        validateTheoLoaiDon(hd);

        HoaDon saved = repo.save(hd);

        pushHistory(saved.getId(), saved.getTrangThaiHienTai(), "Tạo đơn");

        return toResponse(saved);
    }

    /**
     * POS: CHỐT ĐƠN TẠI QUẦY - TIỀN MẶT
     * - re-validate tồn (atomic bằng update có điều kiện)
     * - re-validate voucher (tổng + cá nhân) + consume
     * - tính lại tổng tiền snapshot
     * - tạo giao dịch thanh toán tiền mặt
     * - HOÀN THÀNH + push lịch sử
     */
    @Transactional
    public HoaDonResponse confirmTaiQuayTienMat(Integer idHoaDon, String note) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id hóa đơn");

        HoaDon hd = repo.findByIdAndXoaMemFalse(idHoaDon)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDon id=" + idHoaDon));

        if (TrangThaiHoaDon.isTerminal(hd.getTrangThaiHienTai())) {
            return toResponse(hd);
        }

        // ép loại đơn tại quầy
        hd.setLoaiDon(false);
        if (hd.getPhiVanChuyen() == null) hd.setPhiVanChuyen(BigDecimal.ZERO);

        // 1) load chi tiết
        List<HoaDonChiTiet> items = hoaDonChiTietRepository.findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(idHoaDon);
        if (items == null || items.isEmpty()) {
            throw new BadRequestEx("Hóa đơn chưa có sản phẩm, không thể chốt");
        }

        // 2) tính tổng tiền hàng từ snapshot donGia*soLuong
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

        // 3) re-validate & consume voucher (tính tiền giảm)
        BigDecimal tienGiam = tinhVaConsumeVoucherNeuCo(hd, tongTienHang);

        // 4) tổng tiền (tại quầy phi ship = 0)
        BigDecimal tongTien = tongTienHang.add(hd.getPhiVanChuyen() == null ? BigDecimal.ZERO : hd.getPhiVanChuyen());
        BigDecimal tongTienSauGiam = tongTien.subtract(tienGiam);
        if (tongTienSauGiam.signum() < 0) tongTienSauGiam = BigDecimal.ZERO;

        hd.setTongTien(tongTien);
        hd.setTongTienSauGiam(tongTienSauGiam);

        // 5) re-validate & TRỪ TỒN atomic
        for (HoaDonChiTiet ct : items) {
            Integer ctspId = ct.getIdChiTietSanPham();
            Integer qty = ct.getSoLuong();
            if (ctspId == null) throw new BadRequestEx("Thiếu id_chi_tiet_san_pham ở chi tiết hóa đơn id=" + ct.getId());

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

        // 6) tạo giao dịch thanh toán tiền mặt
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

        // 7) cập nhật hóa đơn -> hoàn thành
        hd.setTrangThaiHienTai(TrangThaiHoaDon.HOAN_THANH.code);
        hd.setNgayThanhToan(LocalDateTime.now());
        hd.setNgayCapNhat(LocalDateTime.now());

        validateTheoLoaiDon(hd);

        HoaDon saved = repo.save(hd);

        // 8) lịch sử
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

        if (newStatus < 1 || newStatus > 7) {
            throw new BadRequestEx("trang_thai_hien_tai không hợp lệ (1..7)");
        }

        if (newStatus < oldStatus) {
            throw new BadRequestEx("Không thể chuyển trạng thái lùi");
        }

        if (newStatus == TrangThaiHoaDon.GIAO_THAT_BAI.code) {
            if (Boolean.FALSE.equals(hd.getLoaiDon())) {
                throw new BadRequestEx("Đơn tại quầy không có trạng thái giao thất bại");
            }
            if (!(oldStatus == TrangThaiHoaDon.CHO_GIAO_HANG.code
                    || oldStatus == TrangThaiHoaDon.DANG_VAN_CHUYEN.code
                    || oldStatus == TrangThaiHoaDon.DA_GIAO_HANG.code)) {
                throw new BadRequestEx("Chỉ được chuyển giao thất bại khi đang giao hàng");
            }
        }

        hd.setTrangThaiHienTai(newStatus);

        if ((newStatus == TrangThaiHoaDon.DA_THANH_TOAN.code
                || newStatus == TrangThaiHoaDon.HOAN_THANH.code)
                && hd.getNgayThanhToan() == null) {
            hd.setNgayThanhToan(LocalDateTime.now());
        }

        hd.setNgayCapNhat(LocalDateTime.now());
        HoaDon saved = repo.save(hd);

        pushHistory(saved.getId(), newStatus,
                (note == null || note.isBlank()) ? "Cập nhật trạng thái" : note.trim());

        return toResponse(saved);
    }

    /**
     * Endpoint cũ: giữ lại để admin dùng, POS nên dùng confirmTaiQuayTienMat.
     */
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

        if (hd.getLoaiDon() == null) hd.setLoaiDon(false); // default: tại quầy
        if (hd.getPhiVanChuyen() == null) hd.setPhiVanChuyen(BigDecimal.ZERO);

        if (hd.getTenKhachHang() != null) hd.setTenKhachHang(hd.getTenKhachHang().trim());
        if (hd.getDiaChiKhachHang() != null) hd.setDiaChiKhachHang(hd.getDiaChiKhachHang().trim());
        if (hd.getSoDienThoaiKhachHang() != null) hd.setSoDienThoaiKhachHang(hd.getSoDienThoaiKhachHang().trim());
        if (hd.getEmailKhachHang() != null) hd.setEmailKhachHang(hd.getEmailKhachHang().trim());
        if (hd.getGhiChu() != null) hd.setGhiChu(hd.getGhiChu().trim());

        if (hd.getTongTien() == null) hd.setTongTien(BigDecimal.ZERO);
        if (hd.getTongTienSauGiam() == null) hd.setTongTienSauGiam(BigDecimal.ZERO);
    }

    // ✅ validate theo loại đơn (POS thực tế)
    private void validateTheoLoaiDon(HoaDon hd) {
        // luôn cần tên + sdt (tuỳ bạn muốn cho khách lẻ trống hay không)
        if (hd.getTenKhachHang() == null || hd.getTenKhachHang().isBlank())
            throw new BadRequestEx("Thiếu ten_khach_hang");
        if (hd.getSoDienThoaiKhachHang() == null || hd.getSoDienThoaiKhachHang().isBlank())
            throw new BadRequestEx("Thiếu so_dien_thoai_khach_hang");

        // giao hàng mới bắt buộc địa chỉ
        if (Boolean.TRUE.equals(hd.getLoaiDon())) {
            if (hd.getDiaChiKhachHang() == null || hd.getDiaChiKhachHang().isBlank())
                throw new BadRequestEx("Thiếu dia_chi_khach_hang (đơn giao hàng)");
        } else {
            if (hd.getDiaChiKhachHang() == null) hd.setDiaChiKhachHang("");
        }

        if (hd.getTongTien() == null || hd.getTongTien().signum() < 0)
            throw new BadRequestEx("tong_tien không hợp lệ");
        if (hd.getTongTienSauGiam() == null || hd.getTongTienSauGiam().signum() < 0)
            throw new BadRequestEx("tong_tien_sau_giam không hợp lệ");
        if (hd.getTongTienSauGiam().compareTo(hd.getTongTien()) > 0)
            throw new BadRequestEx("tong_tien_sau_giam không được lớn hơn tong_tien");

        if (hd.getTrangThaiHienTai() == null)
            throw new BadRequestEx("Thiếu trang_thai_hien_tai");

        if (hd.getTrangThaiHienTai() < 1 || hd.getTrangThaiHienTai() > 7)
            throw new BadRequestEx("trang_thai_hien_tai không hợp lệ (1..7)");
    }

    /**
     * Tính tiền giảm & consume voucher nếu có.
     * - Voucher cá nhân: markUsed + consume voucher tổng (so_luong_su_dung - 1)
     * - Voucher tổng: consume (so_luong_su_dung - 1)
     */
    private BigDecimal tinhVaConsumeVoucherNeuCo(HoaDon hd, BigDecimal tongTienHang) {
        BigDecimal giam = BigDecimal.ZERO;
        LocalDate today = LocalDate.now();

        // --- 1) Voucher cá nhân ---
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

            // đồng bộ id voucher tổng để đúng FK ghép của bạn
            if (hd.getIdPhieuGiamGia() == null) {
                hd.setIdPhieuGiamGia(pgg.getId());
            } else if (!hd.getIdPhieuGiamGia().equals(pgg.getId())) {
                throw new BadRequestEx("Voucher cá nhân không khớp id_phieu_giam_gia trên hóa đơn");
            }

            giam = tinhSoTienGiam(pgg, tongTienHang);

            // consume số lượt còn lại của voucher tổng
            int okConsume = phieuGiamGiaRepository.consumeNeuCon(pgg.getId());
            if (okConsume == 0) {
                throw new BadRequestEx("Phiếu giảm giá đã hết lượt sử dụng");
            }

            // mark used voucher cá nhân
            int okMark = phieuGiamGiaCaNhanRepository.markUsedNeuHopLe(caNhan.getId(), hd.getIdKhachHang());
            if (okMark == 0) {
                throw new BadRequestEx("Không thể đánh dấu dùng voucher cá nhân (có thể đã dùng/không hợp lệ)");
            }

            return giam;
        }

        // --- 2) Voucher tổng ---
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

    /**
     * ✅ Mapping đúng theo DB:
     * - loai_phieu_giam_gia = 0 => giảm %
     * - loai_phieu_giam_gia = 1 => giảm số tiền
     *
     * Java Boolean:
     * - false => 0 => %
     * - true  => 1 => tiền
     */
    private BigDecimal tinhSoTienGiam(PhieuGiamGia pgg, BigDecimal tongTienHang) {
        BigDecimal giam;

        if (Boolean.FALSE.equals(pgg.getLoaiPhieuGiamGia())) {
            // %: giaTriGiamGia là % (vd 10 = 10%)
            BigDecimal percent = pgg.getGiaTriGiamGia();
            if (percent == null || percent.signum() <= 0) return BigDecimal.ZERO;

            giam = tongTienHang.multiply(percent).divide(BigDecimal.valueOf(100));
        } else {
            // tiền: giaTriGiamGia là số tiền giảm
            giam = pgg.getGiaTriGiamGia();
            if (giam == null || giam.signum() <= 0) return BigDecimal.ZERO;
        }

        // cap tối đa nếu có
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
