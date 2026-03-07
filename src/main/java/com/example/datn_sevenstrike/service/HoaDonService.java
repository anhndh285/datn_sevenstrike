// File: src/main/java/com/example/datn_sevenstrike/service/HoaDonService.java
package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.constants.TrangThaiHoaDon;
import com.example.datn_sevenstrike.dto.request.HoaDonChiTietRequest;
import com.example.datn_sevenstrike.dto.request.HoaDonRequest;
import com.example.datn_sevenstrike.dto.request.XacNhanThanhToanTaiQuayRequest;
import com.example.datn_sevenstrike.dto.response.HoaDonChiTietResponse;
import com.example.datn_sevenstrike.dto.response.HoaDonResponse;
import com.example.datn_sevenstrike.entity.*;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.*;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HoaDonService {

    private static final int LOAI_DON_TAI_QUAY = 0;
    private static final int LOAI_DON_GIAO_HANG = 1;
    private static final int LOAI_DON_ONLINE = 2;

    private static final String PTTT_TIEN_MAT = "Tiền mặt";
    private static final String PTTT_CHUYEN_KHOAN = "Chuyển khoản";

    private final HoaDonRepository repo;
    private final HoaDonChiTietRepository hoaDonChiTietRepository;
    private final LichSuHoaDonRepository lichSuRepo;

    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final ChiTietDotGiamGiaRepository chiTietDotGiamGiaRepository;

    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final PhieuGiamGiaCaNhanRepository phieuGiamGiaCaNhanRepository;

    private final GiaoDichThanhToanRepository giaoDichThanhToanRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    // ✅ Thêm giao ca để tự động gắn id_giao_ca khi tạo/cập nhật hóa đơn POS
    private final GiaoCaRepository giaoCaRepo;

    private final ModelMapper mapper;

    @PersistenceContext
    private EntityManager em;

    // =========================================================
    // ========================= VIEW ==========================
    // =========================================================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LichSuThaoTacView {
        private LocalDateTime thoiGian;
        private Integer trangThai;
        private String noiDung;

        private Integer nguoiCapNhat;
        private String maNhanVien;
        private String tenTaiKhoan;
        private String nguoiThaoTac;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LichSuThanhToanView {
        private LocalDateTime thoiGian;
        private String loai;
        private BigDecimal soTien;
        private String ghiChu;

        private Integer nguoiCapNhat;
        private String maNhanVien;
        private String tenTaiKhoan;
        private String nguoiThaoTac;
    }

    private LocalDateTime toLocalDateTime(Object x) {
        if (x == null) return null;
        if (x instanceof LocalDateTime) return (LocalDateTime) x;
        if (x instanceof Timestamp) return ((Timestamp) x).toLocalDateTime();
        return null;
    }

    private String buildNguoiThaoTac(String maNhanVien, String tenTaiKhoan) {
        String ma = (maNhanVien == null) ? "" : maNhanVien.trim();
        String tk = (tenTaiKhoan == null) ? "" : tenTaiKhoan.trim();

        if (!ma.isBlank() && !tk.isBlank()) return ma + " - " + tk;
        if (!ma.isBlank()) return ma;
        return "";
    }

    // =========================================================
    // ====================== RESET (POS) =======================
    // =========================================================

    @Transactional
    public void resetHoaDonChoTaiQuay(Integer idHoaDon) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id hóa đơn");

        HoaDon hd = repo.findByIdAndXoaMemFalse(idHoaDon)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy hóa đơn id=" + idHoaDon));

        Integer loaiDon = hd.getLoaiDon() == null ? LOAI_DON_TAI_QUAY : hd.getLoaiDon();
        Integer st = hd.getTrangThaiHienTai() == null ? TrangThaiHoaDon.CHUA_XAC_NHAN.code : hd.getTrangThaiHienTai();

        if (loaiDon != LOAI_DON_TAI_QUAY || st != TrangThaiHoaDon.CHUA_XAC_NHAN.code) {
            throw new BadRequestEx("Chỉ được reset hóa đơn chờ tại quầy (loại đơn tại quầy + chưa xác nhận)");
        }

        hoanTonTheoHoaDon(idHoaDon);
        hardDeleteHoaDon(idHoaDon);
    }

    @Transactional
    public void resetHoaDonChoTaiQuayCuNgay(LocalDateTime startToday) {
        LocalDateTime cutoff = (startToday == null) ? LocalDate.now().atStartOfDay() : startToday;

        List<Integer> ids = repo.findIdsChoTaiQuayCanReset(
                LOAI_DON_TAI_QUAY,
                TrangThaiHoaDon.CHUA_XAC_NHAN.code,
                cutoff
        );
        if (ids == null || ids.isEmpty()) return;

        for (Integer idHoaDon : ids) {
            if (idHoaDon == null) continue;
            hoanTonTheoHoaDon(idHoaDon);
            hardDeleteHoaDon(idHoaDon);
        }
    }

    private void hardDeleteHoaDon(Integer idHoaDon) {
        giaoDichThanhToanRepository.deleteHardByIdHoaDon(idHoaDon);
        hoaDonChiTietRepository.deleteHardByIdHoaDon(idHoaDon);
        lichSuRepo.deleteHardByIdHoaDon(idHoaDon);
        repo.deleteHardById(idHoaDon);
    }

    private void hoanTonTheoHoaDon(Integer idHoaDon) {
        List<HoaDonChiTiet> items = hoaDonChiTietRepository.findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(idHoaDon);
        if (items == null || items.isEmpty()) return;

        for (HoaDonChiTiet ct : items) {
            Integer ctspId = ct.getIdChiTietSanPham();
            Integer qty = ct.getSoLuong();
            if (ctspId == null || qty == null || qty <= 0) continue;
            chiTietSanPhamRepository.tangTon(ctspId, qty);
        }
    }

    // =========================================================
    // ===================== QUẢN LÝ HĐ =========================
    // =========================================================

    public List<HoaDonResponse> all() {
        List<HoaDonResponse> list = repo.getDanhSachHoaDon();
        if (list != null) {
            for (HoaDonResponse x : list) {
                x.setTrangThaiLabel(toTrangThaiLabel(x.getTrangThaiHienTai()));
            }
        }
        return list;
    }

    public Page<HoaDonResponse> page(int pageNo, int pageSize) {
        int p = Math.max(pageNo, 0);
        int s = Math.max(pageSize, 1);
        var pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "id"));
        return repo.pageQuanLyHoaDon(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public HoaDonResponse one(Integer id) {
        HoaDon hd = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy hóa đơn id=" + id));

        HoaDonResponse res = toResponse(hd);

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
                        if (ms != null) mauSac = ms.getTenMauSac();

                        KichThuoc kt = ctsp.getKichThuoc();
                        if (kt != null) {
                            if (kt.getGiaTriKichThuoc() != null) kichCo = String.valueOf(kt.getGiaTriKichThuoc());
                            else kichCo = kt.getTenKichThuoc();
                        }

                        LoaiSan ls = ctsp.getLoaiSan();
                        if (ls != null) loaiSan = ls.getTenLoaiSan();

                        FormChan fc = ctsp.getFormChan();
                        if (fc != null) formChan = fc.getTenFormChan();
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

    // =========================================================
    // ========================= LỊCH SỬ ========================
    // =========================================================

    @Transactional(readOnly = true)
    public List<LichSuThaoTacView> lichSuThaoTac(Integer idHoaDon) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id hóa đơn");

        String sql = """
                select
                    ls.thoi_gian,
                    ls.trang_thai,
                    ls.ghi_chu,
                    ls.nguoi_cap_nhat,
                    nv.ma_nhan_vien,
                    nv.ten_tai_khoan
                from dbo.lich_su_hoa_don ls
                left join dbo.nhan_vien nv
                    on nv.id = ls.nguoi_cap_nhat
                   and nv.xoa_mem = 0
                where ls.xoa_mem = 0
                  and ls.id_hoa_don = :idHoaDon
                order by ls.thoi_gian desc, ls.id desc
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("idHoaDon", idHoaDon)
                .getResultList();

        return rows.stream().map(r -> {
            LocalDateTime thoiGian = toLocalDateTime(r[0]);
            Integer trangThai = r[1] == null ? null : ((Number) r[1]).intValue();
            String ghiChu = r[2] == null ? null : String.valueOf(r[2]);

            Integer nguoiCapNhat = r[3] == null ? null : ((Number) r[3]).intValue();
            String maNhanVien = r[4] == null ? null : String.valueOf(r[4]);
            String tenTaiKhoan = r[5] == null ? null : String.valueOf(r[5]);

            return LichSuThaoTacView.builder()
                    .thoiGian(thoiGian)
                    .trangThai(trangThai)
                    .noiDung(ghiChu)
                    .nguoiCapNhat(nguoiCapNhat)
                    .maNhanVien(maNhanVien)
                    .tenTaiKhoan(tenTaiKhoan)
                    .nguoiThaoTac(buildNguoiThaoTac(maNhanVien, tenTaiKhoan))
                    .build();
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<LichSuThanhToanView> lichSuThanhToan(Integer idHoaDon) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id hóa đơn");

        String sql = """
                select
                    gd.thoi_gian_tao,
                    pttt.ten_phuong_thuc_thanh_toan,
                    gd.so_tien,
                    gd.ghi_chu,
                    gd.nguoi_cap_nhat,
                    nv.ma_nhan_vien,
                    nv.ten_tai_khoan
                from dbo.giao_dich_thanh_toan gd
                join dbo.phuong_thuc_thanh_toan pttt
                    on pttt.id = gd.id_phuong_thuc_thanh_toan
                   and pttt.xoa_mem = 0
                left join dbo.nhan_vien nv
                    on nv.id = gd.nguoi_cap_nhat
                   and nv.xoa_mem = 0
                where gd.xoa_mem = 0
                  and gd.id_hoa_don = :idHoaDon
                order by gd.thoi_gian_tao desc, gd.id desc
                """;

        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(sql)
                .setParameter("idHoaDon", idHoaDon)
                .getResultList();

        return rows.stream().map(r -> {
            LocalDateTime thoiGian = toLocalDateTime(r[0]);
            String loai = r[1] == null ? null : String.valueOf(r[1]);
            BigDecimal soTien = r[2] == null ? BigDecimal.ZERO : (BigDecimal) r[2];
            String ghiChu = r[3] == null ? null : String.valueOf(r[3]);

            Integer nguoiCapNhat = r[4] == null ? null : ((Number) r[4]).intValue();
            String maNhanVien = r[5] == null ? null : String.valueOf(r[5]);
            String tenTaiKhoan = r[6] == null ? null : String.valueOf(r[6]);

            return LichSuThanhToanView.builder()
                    .thoiGian(thoiGian)
                    .loai(loai)
                    .soTien(soTien)
                    .ghiChu(ghiChu)
                    .nguoiCapNhat(nguoiCapNhat)
                    .maNhanVien(maNhanVien)
                    .tenTaiKhoan(tenTaiKhoan)
                    .nguoiThaoTac(buildNguoiThaoTac(maNhanVien, tenTaiKhoan))
                    .build();
        }).toList();
    }

    // =========================================================
    // ========================= CREATE =========================
    // =========================================================

    @Transactional
    public HoaDonResponse create(HoaDonRequest req) {
        return create(req, null);
    }

    @Transactional
    public HoaDonResponse create(HoaDonRequest req, Integer nguoiCapNhat) {
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu tạo mới");

        HoaDon hd = mapper.map(req, HoaDon.class);
        hd.setId(null);

        // ✅ Tự động gắn ca đang hoạt động, không có ca thì để null để không chặn admin
        if (hd.getIdNhanVien() != null) {
            giaoCaRepo.findCaDangHoatDong(hd.getIdNhanVien())
                    .ifPresentOrElse(
                            ca -> hd.setIdGiaoCa(ca.getId()),
                            () -> hd.setIdGiaoCa(null)
                    );
        } else {
            hd.setIdGiaoCa(null);
        }

        applyDefaults(hd);

        if (hd.getTrangThaiHienTai() == null) {
            hd.setTrangThaiHienTai(TrangThaiHoaDon.CHUA_XAC_NHAN.code);
        }

        if (nguoiCapNhat != null) {
            if (hd.getNguoiTao() == null) hd.setNguoiTao(nguoiCapNhat);
            hd.setNguoiCapNhat(nguoiCapNhat);
        }

        syncVoucherCaNhanComboTruocKhiLuu(hd);
        validateTheoLoaiDon(hd);

        HoaDon saved = repo.save(hd);
        pushHistory(saved.getId(), saved.getTrangThaiHienTai(), "Tạo đơn", nguoiCapNhat);

        return toResponse(saved);
    }

    @Transactional
    public HoaDonResponse capNhatThongTinPos(Integer idHoaDon, HoaDonRequest req) {
        return capNhatThongTinPos(idHoaDon, req, null);
    }

    @Transactional
    public HoaDonResponse capNhatThongTinPos(Integer idHoaDon, HoaDonRequest req, Integer nguoiCapNhat) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id hóa đơn");
        if (req == null) throw new BadRequestEx("Thiếu dữ liệu cập nhật");

        HoaDon hd = repo.findByIdAndXoaMemFalse(idHoaDon)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy hóa đơn id=" + idHoaDon));

        if (TrangThaiHoaDon.isTerminal(hd.getTrangThaiHienTai())) {
            throw new BadRequestEx("Đơn đã kết thúc, không thể cập nhật thông tin");
        }

        if (req.getIdKhachHang() != null) hd.setIdKhachHang(req.getIdKhachHang());
        if (req.getIdNhanVien() != null) hd.setIdNhanVien(req.getIdNhanVien());

        // ✅ Dọn và gắn lại id_giao_ca theo ca đang hoạt động của nhân viên hiện tại
        if (hd.getIdNhanVien() != null) {
            giaoCaRepo.findCaDangHoatDong(hd.getIdNhanVien())
                    .ifPresentOrElse(
                            ca -> hd.setIdGiaoCa(ca.getId()),
                            () -> hd.setIdGiaoCa(null)
                    );
        } else {
            hd.setIdGiaoCa(null);
        }

        if (req.getIdPhieuGiamGia() != null) hd.setIdPhieuGiamGia(req.getIdPhieuGiamGia());
        if (req.getIdPhieuGiamGiaCaNhan() != null) hd.setIdPhieuGiamGiaCaNhan(req.getIdPhieuGiamGiaCaNhan());

        if (req.getLoaiDon() != null) hd.setLoaiDon(req.getLoaiDon());

        if (req.getPhiVanChuyen() != null) hd.setPhiVanChuyen(req.getPhiVanChuyen());
        if (req.getTongTien() != null) hd.setTongTien(req.getTongTien());
        if (req.getTongTienSauGiam() != null) hd.setTongTienSauGiam(req.getTongTienSauGiam());

        if (req.getTenKhachHang() != null) hd.setTenKhachHang(req.getTenKhachHang());
        if (req.getDiaChiKhachHang() != null) hd.setDiaChiKhachHang(req.getDiaChiKhachHang());
        if (req.getSoDienThoaiKhachHang() != null) hd.setSoDienThoaiKhachHang(req.getSoDienThoaiKhachHang());
        if (req.getEmailKhachHang() != null) hd.setEmailKhachHang(req.getEmailKhachHang());

        if (req.getGhiChu() != null) hd.setGhiChu(req.getGhiChu());

        if (nguoiCapNhat != null) hd.setNguoiCapNhat(nguoiCapNhat);

        applyDefaults(hd);
        validateThongTinPos(hd);

        hd.setNgayCapNhat(LocalDateTime.now());
        HoaDon saved = repo.save(hd);

        pushHistory(idHoaDon, hd.getTrangThaiHienTai(), "Cập nhật thông tin hóa đơn", nguoiCapNhat);

        return toResponse(saved);
    }

    private void validateThongTinPos(HoaDon hd) {
        Integer loaiDon = hd.getLoaiDon();
        if (loaiDon == null) loaiDon = LOAI_DON_TAI_QUAY;

        if (loaiDon != LOAI_DON_TAI_QUAY && loaiDon != LOAI_DON_GIAO_HANG && loaiDon != LOAI_DON_ONLINE) {
            throw new BadRequestEx("loai_don không hợp lệ (0/1/2)");
        }

        if (hd.getPhiVanChuyen() != null && hd.getPhiVanChuyen().signum() < 0) {
            throw new BadRequestEx("phi_van_chuyen không hợp lệ");
        }

        if (hd.getTongTien() != null && hd.getTongTien().signum() < 0) {
            throw new BadRequestEx("tong_tien không hợp lệ");
        }
        if (hd.getTongTienSauGiam() != null && hd.getTongTienSauGiam().signum() < 0) {
            throw new BadRequestEx("tong_tien_sau_giam không hợp lệ");
        }
        if (hd.getTongTien() != null && hd.getTongTienSauGiam() != null
                && hd.getTongTienSauGiam().compareTo(hd.getTongTien()) > 0) {
            throw new BadRequestEx("tong_tien_sau_giam không được lớn hơn tong_tien");
        }
    }

    private void syncVoucherCaNhanComboTruocKhiLuu(HoaDon hd) {
        if (hd.getIdPhieuGiamGiaCaNhan() == null) return;

        if (hd.getIdKhachHang() == null) {
            throw new BadRequestEx("Đơn dùng voucher cá nhân nhưng thiếu id_khach_hang");
        }

        PhieuGiamGiaCaNhan caNhan = phieuGiamGiaCaNhanRepository
                .findByIdAndXoaMemFalse(hd.getIdPhieuGiamGiaCaNhan())
                .orElseThrow(() -> new BadRequestEx("Voucher cá nhân không tồn tại hoặc đã xóa"));

        if (!hd.getIdKhachHang().equals(caNhan.getIdKhachHang())) {
            throw new BadRequestEx("Voucher cá nhân không thuộc khách hàng này");
        }

        Integer pggId = caNhan.getIdPhieuGiamGia();
        if (pggId == null) {
            throw new BadRequestEx("Voucher cá nhân thiếu id_phieu_giam_gia");
        }

        if (hd.getIdPhieuGiamGia() == null) {
            hd.setIdPhieuGiamGia(pggId);
        } else if (!hd.getIdPhieuGiamGia().equals(pggId)) {
            throw new BadRequestEx("Voucher cá nhân không khớp id_phieu_giam_gia trên hóa đơn");
        }
    }

    // =========================================================
    // ===================== UPSERT CHI TIẾT ====================
    // =========================================================

    @Transactional
    public HoaDonResponse upsertChiTiet(Integer idHoaDon, List<HoaDonChiTietRequest> items) {
        return upsertChiTiet(idHoaDon, items, null);
    }

    @Transactional
    public HoaDonResponse upsertChiTiet(Integer idHoaDon, List<HoaDonChiTietRequest> items, Integer nguoiCapNhat) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id hóa đơn");
        if (items == null) throw new BadRequestEx("Thiếu danh sách sản phẩm");

        HoaDon hd = repo.findByIdAndXoaMemFalse(idHoaDon)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDon id=" + idHoaDon));

        if (TrangThaiHoaDon.isTerminal(hd.getTrangThaiHienTai())) {
            throw new BadRequestEx("Đơn đã kết thúc, không thể cập nhật sản phẩm");
        }

        List<HoaDonChiTiet> current = hoaDonChiTietRepository.findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(idHoaDon);

        Map<Integer, Integer> oldQtyMap = new HashMap<>();
        for (HoaDonChiTiet ct : current) {
            Integer ctspId = ct.getIdChiTietSanPham();
            if (ctspId == null) continue;
            int qty = ct.getSoLuong() == null ? 0 : ct.getSoLuong();
            if (qty < 0) qty = 0;
            oldQtyMap.put(ctspId, qty);
        }

        Map<Integer, Integer> newQtyMap = new HashMap<>();
        Map<Integer, String> newGhiChuMap = new HashMap<>();

        for (HoaDonChiTietRequest x : items) {
            if (x == null) throw new BadRequestEx("Dữ liệu sản phẩm không hợp lệ");

            if (x.getIdHoaDon() != null && !idHoaDon.equals(x.getIdHoaDon())) {
                throw new BadRequestEx("idHoaDon trong body không khớp với đường dẫn");
            }

            Integer ctspId = x.getIdChiTietSanPham();
            if (ctspId == null) throw new BadRequestEx("Thiếu idChiTietSanPham");

            if (Boolean.TRUE.equals(x.getXoaMem())) {
                newQtyMap.put(ctspId, 0);
                continue;
            }

            if (x.getSoLuong() == null || x.getSoLuong() <= 0) {
                throw new BadRequestEx("Số lượng phải lớn hơn 0");
            }

            int sum = newQtyMap.get(ctspId) == null ? 0 : newQtyMap.get(ctspId);
            newQtyMap.put(ctspId, sum + x.getSoLuong());

            String ghiChu = (x.getGhiChu() == null || x.getGhiChu().isBlank()) ? null : x.getGhiChu().trim();
            if (ghiChu != null) newGhiChuMap.put(ctspId, ghiChu);
        }

        Map<Integer, Integer> union = new HashMap<>(oldQtyMap);
        for (Integer k : newQtyMap.keySet()) union.put(k, 0);

        for (Integer ctspId : union.keySet()) {
            int oldQty = oldQtyMap.get(ctspId) == null ? 0 : oldQtyMap.get(ctspId);
            int newQty = newQtyMap.get(ctspId) == null ? 0 : newQtyMap.get(ctspId);

            int delta = newQty - oldQty;
            if (delta > 0) {
                int updated = chiTietSanPhamRepository.giamTonNeuDu(ctspId, delta);
                if (updated == 0) {
                    ChiTietSanPham ctsp = chiTietSanPhamRepository.findByIdAndXoaMemFalse(ctspId)
                            .orElseThrow(() -> new BadRequestEx("CTSP không tồn tại hoặc đã xóa: id=" + ctspId));

                    String ma = ctsp.getMaChiTietSanPham();
                    Integer ton = ctsp.getSoLuong();
                    throw new BadRequestEx("Không đủ tồn kho cho CTSP " + (ma == null ? ("id=" + ctspId) : ma)
                            + " (tồn hiện tại: " + (ton == null ? 0 : ton) + ")");
                }
            } else if (delta < 0) {
                chiTietSanPhamRepository.tangTon(ctspId, Math.abs(delta));
            }
        }

        for (HoaDonChiTiet old : current) {
            Integer ctspId = old.getIdChiTietSanPham();
            if (ctspId == null) continue;

            int newQty = newQtyMap.get(ctspId) == null ? 0 : newQtyMap.get(ctspId);
            if (newQty <= 0) {
                old.setXoaMem(true);
                continue;
            }

            ChiTietSanPham ctsp = chiTietSanPhamRepository.findByIdAndXoaMemFalse(ctspId)
                    .orElseThrow(() -> new BadRequestEx("CTSP không tồn tại hoặc đã xóa: id=" + ctspId));

            BigDecimal giaGoc = ctsp.getGiaBan() == null ? BigDecimal.ZERO : ctsp.getGiaBan();
            BigDecimal donGiaApDung = tinhGiaBanSauGiamTheoDot(ctspId, giaGoc);

            old.setSoLuong(newQty);
            old.setDonGia(donGiaApDung);
            old.setGhiChu(newGhiChuMap.get(ctspId));
            old.setXoaMem(false);

            newQtyMap.remove(ctspId);
        }

        for (Map.Entry<Integer, Integer> e : newQtyMap.entrySet()) {
            Integer ctspId = e.getKey();
            Integer qty = e.getValue();
            if (ctspId == null || qty == null || qty <= 0) continue;

            ChiTietSanPham ctsp = chiTietSanPhamRepository.findByIdAndXoaMemFalse(ctspId)
                    .orElseThrow(() -> new BadRequestEx("CTSP không tồn tại hoặc đã xóa: id=" + ctspId));

            BigDecimal giaGoc = ctsp.getGiaBan() == null ? BigDecimal.ZERO : ctsp.getGiaBan();
            BigDecimal donGiaApDung = tinhGiaBanSauGiamTheoDot(ctspId, giaGoc);

            HoaDonChiTiet ctNew = new HoaDonChiTiet();
            ctNew.setId(null);
            ctNew.setIdHoaDon(idHoaDon);
            ctNew.setIdChiTietSanPham(ctspId);
            ctNew.setSoLuong(qty);
            ctNew.setDonGia(donGiaApDung);
            ctNew.setGhiChu(newGhiChuMap.get(ctspId));
            ctNew.setXoaMem(false);

            current.add(ctNew);
        }

        hoaDonChiTietRepository.saveAll(current);

        if (nguoiCapNhat != null) hd.setNguoiCapNhat(nguoiCapNhat);

        hd.setNgayCapNhat(LocalDateTime.now());
        repo.save(hd);

        pushHistory(idHoaDon, hd.getTrangThaiHienTai(), "Cập nhật sản phẩm trong hóa đơn", nguoiCapNhat);

        return one(idHoaDon);
    }

    // =========================================================
    // ==================== CONFIRM TẠI QUẦY ====================
    // =========================================================

    @Transactional
    public HoaDonResponse confirmTaiQuayTienMat(Integer idHoaDon, String note, Integer nguoiCapNhat) {
        XacNhanThanhToanTaiQuayRequest body = XacNhanThanhToanTaiQuayRequest.builder()
                .ghiChu(note)
                .thanhToans(List.of(
                        XacNhanThanhToanTaiQuayRequest.ThanhToanTaiQuayItem.builder()
                                .tenPhuongThuc(PTTT_TIEN_MAT)
                                .soTien(null)
                                .ghiChu(note)
                                .build()
                ))
                .build();
        return confirmTaiQuayKetHop(idHoaDon, body, nguoiCapNhat);
    }

    @Transactional
    public HoaDonResponse confirmTaiQuayChuyenKhoan(Integer idHoaDon, String note, Integer nguoiCapNhat) {
        XacNhanThanhToanTaiQuayRequest body = XacNhanThanhToanTaiQuayRequest.builder()
                .ghiChu(note)
                .thanhToans(List.of(
                        XacNhanThanhToanTaiQuayRequest.ThanhToanTaiQuayItem.builder()
                                .tenPhuongThuc(PTTT_CHUYEN_KHOAN)
                                .soTien(null)
                                .ghiChu(note)
                                .build()
                ))
                .build();
        return confirmTaiQuayKetHop(idHoaDon, body, nguoiCapNhat);
    }

    @Transactional
    public HoaDonResponse confirmTaiQuayKetHop(Integer idHoaDon, XacNhanThanhToanTaiQuayRequest body, Integer nguoiCapNhat) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id hóa đơn");

        HoaDon hd = repo.findByIdAndXoaMemFalse(idHoaDon)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDon id=" + idHoaDon));

        if (TrangThaiHoaDon.isTerminal(hd.getTrangThaiHienTai())) {
            return toResponse(hd);
        }

        Integer loaiDon = hd.getLoaiDon() == null ? LOAI_DON_TAI_QUAY : hd.getLoaiDon();
        if (loaiDon != LOAI_DON_TAI_QUAY) {
            throw new BadRequestEx("Chỉ được chốt tại quầy cho hóa đơn loại 'Tại quầy'");
        }

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

        BigDecimal tongTien = tongTienHang.add(hd.getPhiVanChuyen());
        BigDecimal tongTienSauGiam = tongTien.subtract(tienGiam);
        if (tongTienSauGiam.signum() < 0) tongTienSauGiam = BigDecimal.ZERO;

        if (tongTienSauGiam.signum() <= 0) {
            throw new BadRequestEx("Tổng tiền sau giảm phải > 0 để ghi nhận giao dịch thanh toán");
        }

        hd.setTongTien(tongTien);
        hd.setTongTienSauGiam(tongTienSauGiam);

        List<XacNhanThanhToanTaiQuayRequest.ThanhToanTaiQuayItem> pays =
                (body == null) ? null : body.getThanhToans();

        if (pays == null || pays.isEmpty()) {
            throw new BadRequestEx("Thiếu danh sách thanh toán");
        }

        boolean hasAnySoTien = false;
        for (var p : pays) {
            if (p != null && p.getSoTien() != null) {
                hasAnySoTien = true;
                break;
            }
        }
        if (!hasAnySoTien) {
            pays.get(0).setSoTien(tongTienSauGiam);
        }

        BigDecimal expected = money2(tongTienSauGiam);
        BigDecimal sumPay = BigDecimal.ZERO;

        for (var p : pays) {
            if (p == null) continue;

            String tenPttt = (p.getTenPhuongThuc() == null) ? "" : p.getTenPhuongThuc().trim();
            if (tenPttt.isBlank()) throw new BadRequestEx("Thiếu tên phương thức thanh toán");

            BigDecimal soTien = p.getSoTien();
            if (soTien == null || soTien.signum() <= 0) {
                throw new BadRequestEx("Số tiền thanh toán phải > 0");
            }

            sumPay = sumPay.add(money2(soTien));
        }

        if (money2(sumPay).compareTo(expected) != 0) {
            throw new BadRequestEx("Tổng tiền thanh toán phải đúng bằng tổng tiền sau giảm (" + expected + ")");
        }

        giaoDichThanhToanRepository.deleteHardByIdHoaDon(idHoaDon);

        String note = (body == null || body.getGhiChu() == null || body.getGhiChu().isBlank())
                ? null
                : body.getGhiChu().trim();

        for (var p : pays) {
            if (p == null) continue;

            String tenPttt = p.getTenPhuongThuc() == null ? "" : p.getTenPhuongThuc().trim();
            BigDecimal soTien = money2(p.getSoTien());

            PhuongThucThanhToan pttt = phuongThucThanhToanRepository
                    .findFirstByTenPhuongThucThanhToanIgnoreCaseAndTrangThaiTrueAndXoaMemFalse(tenPttt)
                    .orElseThrow(() -> new BadRequestEx("Chưa cấu hình phương thức thanh toán '" + tenPttt + "'"));

            String gdGhiChu = (p.getGhiChu() != null && !p.getGhiChu().isBlank())
                    ? p.getGhiChu().trim()
                    : (note != null ? note : ("Thanh toán " + tenPttt));

            GiaoDichThanhToan gd = new GiaoDichThanhToan();
            gd.setId(null);
            gd.setIdHoaDon(idHoaDon);
            gd.setIdPhuongThucThanhToan(pttt.getId());
            gd.setSoTien(soTien);
            gd.setTrangThai("thanh_cong");
            gd.setMaThamChieu((p.getMaThamChieu() == null || p.getMaThamChieu().isBlank()) ? null : p.getMaThamChieu().trim());
            gd.setThoiGianCapNhat(LocalDateTime.now());
            gd.setXoaMem(false);
            gd.setGhiChu(gdGhiChu);
            gd.setNguoiCapNhat(nguoiCapNhat);

            giaoDichThanhToanRepository.save(gd);
        }

        hd.setTrangThaiHienTai(TrangThaiHoaDon.DA_HOAN_THANH.code);
        hd.setNgayThanhToan(LocalDateTime.now());
        hd.setNgayCapNhat(LocalDateTime.now());

        if (nguoiCapNhat != null) hd.setNguoiCapNhat(nguoiCapNhat);

        validateTheoLoaiDon(hd);

        HoaDon saved = repo.save(hd);

        pushHistory(saved.getId(), TrangThaiHoaDon.DA_HOAN_THANH.code,
                (note == null) ? "Chốt đơn tại quầy" : note,
                nguoiCapNhat);

        return toResponse(saved);
    }

    private BigDecimal money2(BigDecimal x) {
        if (x == null) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        return x.setScale(2, RoundingMode.HALF_UP);
    }

    // =========================================================
    // ===== CONFIRM THANH TOÁN CHO GIAO HÀNG / ONLINE =========
    // =========================================================

    private boolean daCoGiaoDichThanhToan(Integer idHoaDon) {
        try {
            if (idHoaDon == null) return false;

            String sql = """
                    select top 1 1
                    from dbo.giao_dich_thanh_toan
                    where xoa_mem = 0
                      and id_hoa_don = :id
                    """;

            return em.createNativeQuery(sql)
                    .setParameter("id", idHoaDon)
                    .getResultStream()
                    .findFirst()
                    .isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public HoaDonResponse confirmGiaoHangKetHop(Integer idHoaDon, XacNhanThanhToanTaiQuayRequest body, Integer nguoiCapNhat) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id hóa đơn");

        HoaDon hd = repo.findByIdAndXoaMemFalse(idHoaDon)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDon id=" + idHoaDon));

        if (TrangThaiHoaDon.isTerminal(hd.getTrangThaiHienTai())) {
            return toResponse(hd);
        }

        Integer loaiDon = hd.getLoaiDon() == null ? LOAI_DON_TAI_QUAY : hd.getLoaiDon();
        if (loaiDon == LOAI_DON_TAI_QUAY) {
            throw new BadRequestEx("Hóa đơn tại quầy vui lòng dùng API chốt tại quầy");
        }

        if (hd.getPhiVanChuyen() == null) hd.setPhiVanChuyen(BigDecimal.ZERO);

        List<HoaDonChiTiet> items = hoaDonChiTietRepository.findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(idHoaDon);
        if (items == null || items.isEmpty()) {
            throw new BadRequestEx("Hóa đơn chưa có sản phẩm, không thể xác nhận thanh toán");
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

        boolean daThanhToanTruocDo = daCoGiaoDichThanhToan(idHoaDon) || hd.getNgayThanhToan() != null;

        BigDecimal tongTien;
        BigDecimal tongTienSauGiam;

        if (!daThanhToanTruocDo) {
            BigDecimal tienGiam = tinhVaConsumeVoucherNeuCo(hd, tongTienHang);

            tongTien = tongTienHang.add(hd.getPhiVanChuyen());
            tongTienSauGiam = tongTien.subtract(tienGiam);
            if (tongTienSauGiam.signum() < 0) tongTienSauGiam = BigDecimal.ZERO;

            hd.setTongTien(tongTien);
            hd.setTongTienSauGiam(tongTienSauGiam);
        } else {
            tongTien = hd.getTongTien() == null ? tongTienHang.add(hd.getPhiVanChuyen()) : hd.getTongTien();
            tongTienSauGiam = hd.getTongTienSauGiam() == null ? tongTien : hd.getTongTienSauGiam();

            hd.setTongTien(tongTien);
            hd.setTongTienSauGiam(tongTienSauGiam);
        }

        if (hd.getTongTienSauGiam() == null || hd.getTongTienSauGiam().signum() <= 0) {
            throw new BadRequestEx("Tổng tiền sau giảm phải > 0 để ghi nhận giao dịch thanh toán");
        }

        List<XacNhanThanhToanTaiQuayRequest.ThanhToanTaiQuayItem> pays =
                (body == null) ? null : body.getThanhToans();

        if (pays == null || pays.isEmpty()) {
            throw new BadRequestEx("Thiếu danh sách thanh toán");
        }

        boolean hasAnySoTien = false;
        for (var p : pays) {
            if (p != null && p.getSoTien() != null) {
                hasAnySoTien = true;
                break;
            }
        }
        if (!hasAnySoTien) {
            pays.get(0).setSoTien(hd.getTongTienSauGiam());
        }

        BigDecimal expected = money2(hd.getTongTienSauGiam());
        BigDecimal sumPay = BigDecimal.ZERO;

        for (var p : pays) {
            if (p == null) continue;

            String tenPttt = (p.getTenPhuongThuc() == null) ? "" : p.getTenPhuongThuc().trim();
            if (tenPttt.isBlank()) throw new BadRequestEx("Thiếu tên phương thức thanh toán");

            BigDecimal soTien = p.getSoTien();
            if (soTien == null || soTien.signum() <= 0) {
                throw new BadRequestEx("Số tiền thanh toán phải > 0");
            }

            sumPay = sumPay.add(money2(soTien));
        }

        if (money2(sumPay).compareTo(expected) != 0) {
            throw new BadRequestEx("Tổng tiền thanh toán phải đúng bằng tổng tiền sau giảm (" + expected + ")");
        }

        giaoDichThanhToanRepository.deleteHardByIdHoaDon(idHoaDon);

        String note = (body == null || body.getGhiChu() == null || body.getGhiChu().isBlank())
                ? null
                : body.getGhiChu().trim();

        for (var p : pays) {
            if (p == null) continue;

            String tenPttt = p.getTenPhuongThuc() == null ? "" : p.getTenPhuongThuc().trim();
            BigDecimal soTien = money2(p.getSoTien());

            PhuongThucThanhToan pttt = phuongThucThanhToanRepository
                    .findFirstByTenPhuongThucThanhToanIgnoreCaseAndTrangThaiTrueAndXoaMemFalse(tenPttt)
                    .orElseThrow(() -> new BadRequestEx("Chưa cấu hình phương thức thanh toán '" + tenPttt + "'"));

            String gdGhiChu = (p.getGhiChu() != null && !p.getGhiChu().isBlank())
                    ? p.getGhiChu().trim()
                    : (note != null ? note : ("Thanh toán " + tenPttt));

            GiaoDichThanhToan gd = new GiaoDichThanhToan();
            gd.setId(null);
            gd.setIdHoaDon(idHoaDon);
            gd.setIdPhuongThucThanhToan(pttt.getId());
            gd.setSoTien(soTien);
            gd.setTrangThai("thanh_cong");
            gd.setMaThamChieu((p.getMaThamChieu() == null || p.getMaThamChieu().isBlank()) ? null : p.getMaThamChieu().trim());
            gd.setThoiGianCapNhat(LocalDateTime.now());
            gd.setXoaMem(false);
            gd.setGhiChu(gdGhiChu);
            gd.setNguoiCapNhat(nguoiCapNhat);

            giaoDichThanhToanRepository.save(gd);
        }

        if (hd.getNgayThanhToan() == null) {
            hd.setNgayThanhToan(LocalDateTime.now());
        }

        hd.setTrangThaiHienTai(TrangThaiHoaDon.DA_XAC_NHAN.code);
        hd.setNgayCapNhat(LocalDateTime.now());

        if (nguoiCapNhat != null) hd.setNguoiCapNhat(nguoiCapNhat);

        validateTheoLoaiDon(hd);

        HoaDon saved = repo.save(hd);

        String noiDungLichSu;
        if (note != null && !note.isBlank()) {
            noiDungLichSu = note;
        } else if (loaiDon == LOAI_DON_GIAO_HANG) {
            noiDungLichSu = "Xác nhận đơn giao hàng";
        } else {
            noiDungLichSu = "Xác nhận đơn online";
        }

        pushHistory(saved.getId(), saved.getTrangThaiHienTai(), noiDungLichSu, nguoiCapNhat);

        return toResponse(saved);
    }

    // =========================================================
    // ===================== CHANGE STATUS ======================
    // =========================================================

    @Transactional
    public HoaDonResponse changeStatus(Integer idHoaDon, Integer newStatus, String note) {
        return changeStatus(idHoaDon, newStatus, note, null);
    }

    @Transactional
    public HoaDonResponse changeStatus(Integer idHoaDon, Integer newStatus, String note, Integer nguoiCapNhat) {
        if (idHoaDon == null) throw new BadRequestEx("Thiếu id hóa đơn");
        if (newStatus == null) throw new BadRequestEx("Thiếu trạng thái mới");

        HoaDon hd = repo.findByIdAndXoaMemFalse(idHoaDon)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDon id=" + idHoaDon));

        Integer oldStatus = hd.getTrangThaiHienTai();
        if (oldStatus == null) oldStatus = TrangThaiHoaDon.CHUA_XAC_NHAN.code;

        if (TrangThaiHoaDon.isTerminal(oldStatus)) {
            throw new BadRequestEx("Đơn đã kết thúc, không thể đổi trạng thái");
        }

        if (!TrangThaiHoaDon.isValid(newStatus)) {
            throw new BadRequestEx("trang_thai_hien_tai không hợp lệ");
        }

        if (newStatus < oldStatus) {
            throw new BadRequestEx("Không thể chuyển trạng thái lùi");
        }

        Integer loaiDon = hd.getLoaiDon();
        if (loaiDon == null) loaiDon = LOAI_DON_TAI_QUAY;

        if (loaiDon == LOAI_DON_TAI_QUAY) {
            boolean hopLeTaiQuay =
                    newStatus.equals(TrangThaiHoaDon.CHUA_XAC_NHAN.code)
                            || newStatus.equals(TrangThaiHoaDon.DA_HOAN_THANH.code)
                            || newStatus.equals(TrangThaiHoaDon.DA_HUY.code);

            if (!hopLeTaiQuay) {
                throw new BadRequestEx("Hóa đơn tại quầy không áp dụng trạng thái giao hàng");
            }
        }

        if (newStatus == TrangThaiHoaDon.DA_HUY.code) {
            cancelAndRestoreStock(hd, note, nguoiCapNhat);
            return one(idHoaDon);
        }

        if (newStatus == TrangThaiHoaDon.DA_HOAN_THANH.code) {
            List<HoaDonChiTiet> items = hoaDonChiTietRepository.findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(idHoaDon);
            if (items == null || items.isEmpty()) {
                throw new BadRequestEx("Hóa đơn chưa có sản phẩm, không thể hoàn thành");
            }
            if (hd.getNgayThanhToan() == null) {
                hd.setNgayThanhToan(LocalDateTime.now());
            }
        }

        hd.setTrangThaiHienTai(newStatus);
        hd.setNgayCapNhat(LocalDateTime.now());

        if (nguoiCapNhat != null) hd.setNguoiCapNhat(nguoiCapNhat);

        HoaDon saved = repo.save(hd);

        pushHistory(saved.getId(), newStatus,
                (note == null || note.isBlank()) ? "Cập nhật trạng thái" : note.trim(),
                nguoiCapNhat);

        return toResponse(saved);
    }

    @Transactional
    public void delete(Integer id, Integer nguoiCapNhat) {
        HoaDon hd = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy HoaDon id=" + id));

        if (TrangThaiHoaDon.isTerminal(hd.getTrangThaiHienTai())) {
            return;
        }

        cancelAndRestoreStock(hd, "Hủy hóa đơn", nguoiCapNhat);
    }

    private void cancelAndRestoreStock(HoaDon hd, String note, Integer nguoiCapNhat) {
        Integer idHoaDon = hd.getId();
        List<HoaDonChiTiet> items = hoaDonChiTietRepository.findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(idHoaDon);

        if (items == null || items.isEmpty()) {
            throw new BadRequestEx("Chỉ được hủy hóa đơn khi hóa đơn có sản phẩm");
        }

        for (HoaDonChiTiet ct : items) {
            Integer ctspId = ct.getIdChiTietSanPham();
            Integer qty = ct.getSoLuong();
            if (ctspId == null || qty == null || qty <= 0) continue;
            chiTietSanPhamRepository.tangTon(ctspId, qty);
        }

        hd.setTrangThaiHienTai(TrangThaiHoaDon.DA_HUY.code);
        hd.setNgayCapNhat(LocalDateTime.now());

        if (nguoiCapNhat != null) hd.setNguoiCapNhat(nguoiCapNhat);

        repo.save(hd);

        pushHistory(idHoaDon, TrangThaiHoaDon.DA_HUY.code,
                (note == null || note.isBlank()) ? "Đã hủy" : note.trim(),
                nguoiCapNhat);
    }

    // =========================================================
    // ========================= PRIVATE ========================
    // =========================================================

    private void pushHistory(Integer idHoaDon, Integer trangThai, String ghiChu, Integer nguoiCapNhat) {
        LichSuHoaDon h = new LichSuHoaDon();
        h.setId(null);
        h.setIdHoaDon(idHoaDon);
        h.setTrangThai(trangThai);
        h.setGhiChu(ghiChu);
        h.setXoaMem(false);
        h.setNguoiCapNhat(nguoiCapNhat);
        lichSuRepo.save(h);
    }

    private void applyDefaults(HoaDon hd) {
        if (hd.getXoaMem() == null) hd.setXoaMem(false);
        if (hd.getNgayTao() == null) hd.setNgayTao(LocalDateTime.now());

        if (hd.getLoaiDon() == null) hd.setLoaiDon(LOAI_DON_TAI_QUAY);
        if (hd.getPhiVanChuyen() == null) hd.setPhiVanChuyen(BigDecimal.ZERO);

        if (hd.getTenKhachHang() != null) hd.setTenKhachHang(hd.getTenKhachHang().trim());
        if (hd.getDiaChiKhachHang() != null) hd.setDiaChiKhachHang(hd.getDiaChiKhachHang().trim());
        if (hd.getSoDienThoaiKhachHang() != null) hd.setSoDienThoaiKhachHang(hd.getSoDienThoaiKhachHang().trim());
        if (hd.getEmailKhachHang() != null) hd.setEmailKhachHang(hd.getEmailKhachHang().trim());
        if (hd.getGhiChu() != null) hd.setGhiChu(hd.getGhiChu().trim());

        if (hd.getTongTien() == null) hd.setTongTien(BigDecimal.ZERO);
        if (hd.getTongTienSauGiam() == null) hd.setTongTienSauGiam(BigDecimal.ZERO);

        if (hd.getDiaChiKhachHang() == null) hd.setDiaChiKhachHang("");

        if (hd.getTenKhachHang() == null || hd.getTenKhachHang().isBlank()) hd.setTenKhachHang("Khách lẻ");
        if (hd.getSoDienThoaiKhachHang() == null || hd.getSoDienThoaiKhachHang().isBlank()) hd.setSoDienThoaiKhachHang("0000000000");
    }

    private void validateTheoLoaiDon(HoaDon hd) {
        if (hd.getTenKhachHang() == null || hd.getTenKhachHang().isBlank()) {
            throw new BadRequestEx("Thiếu ten_khach_hang");
        }
        if (hd.getSoDienThoaiKhachHang() == null || hd.getSoDienThoaiKhachHang().isBlank()) {
            throw new BadRequestEx("Thiếu so_dien_thoai_khach_hang");
        }

        Integer loaiDon = hd.getLoaiDon();
        if (loaiDon == null) loaiDon = LOAI_DON_TAI_QUAY;

        if (loaiDon == LOAI_DON_GIAO_HANG || loaiDon == LOAI_DON_ONLINE) {
            if (hd.getDiaChiKhachHang() == null || hd.getDiaChiKhachHang().isBlank()) {
                throw new BadRequestEx("Thiếu dia_chi_khach_hang (đơn giao hàng/online)");
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
        if (!TrangThaiHoaDon.isValid(hd.getTrangThaiHienTai())) {
            throw new BadRequestEx("trang_thai_hien_tai không hợp lệ");
        }

        if (loaiDon == LOAI_DON_TAI_QUAY) {
            Integer st = hd.getTrangThaiHienTai();
            boolean hopLeTaiQuay =
                    st.equals(TrangThaiHoaDon.CHUA_XAC_NHAN.code)
                            || st.equals(TrangThaiHoaDon.DA_HOAN_THANH.code)
                            || st.equals(TrangThaiHoaDon.DA_HUY.code);

            if (!hopLeTaiQuay) {
                throw new BadRequestEx("Hóa đơn tại quầy không áp dụng trạng thái giao hàng");
            }
        }
    }

    private String toTrangThaiLabel(Integer code) {
        TrangThaiHoaDon t = TrangThaiHoaDon.fromCode(code);
        return t == null ? null : t.label;
    }

    private BigDecimal tinhGiaBanSauGiamTheoDot(Integer ctspId, BigDecimal giaGoc) {
        if (ctspId == null) return giaGoc == null ? BigDecimal.ZERO : giaGoc;

        BigDecimal base = giaGoc == null ? BigDecimal.ZERO : giaGoc;
        if (base.signum() <= 0) return BigDecimal.ZERO;

        LocalDate today = LocalDate.now();
        var bestOpt = chiTietDotGiamGiaRepository.findBestActiveDotByCtspId(ctspId, today);
        if (bestOpt.isEmpty()) return base.setScale(2, RoundingMode.HALF_UP);

        var best = bestOpt.get();
        BigDecimal pct = best.getGiaTriGiam() == null ? BigDecimal.ZERO : best.getGiaTriGiam();
        if (pct.signum() <= 0) return base.setScale(2, RoundingMode.HALF_UP);

        BigDecimal soTienGiam = base.multiply(pct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal max = best.getSoTienGiamToiDa();
        if (max != null && max.signum() > 0 && soTienGiam.compareTo(max) > 0) {
            soTienGiam = max.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal out = base.subtract(soTienGiam);
        if (out.signum() < 0) out = BigDecimal.ZERO;

        return out.setScale(2, RoundingMode.HALF_UP);
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

    private boolean isVoucherPercent(PhieuGiamGia pgg) {
        if (pgg == null) return true;

        Boolean loai = pgg.getLoaiPhieuGiamGia();
        if (Boolean.FALSE.equals(loai)) return true;
        if (Boolean.TRUE.equals(loai)) return false;

        BigDecimal v = pgg.getGiaTriGiamGia();
        if (v != null && v.compareTo(BigDecimal.valueOf(100)) > 0) return false;
        return true;
    }

    private BigDecimal tinhSoTienGiam(PhieuGiamGia pgg, BigDecimal tongTienHang) {
        if (pgg == null) return BigDecimal.ZERO;
        if (tongTienHang == null || tongTienHang.signum() <= 0) return BigDecimal.ZERO;

        BigDecimal giam;

        if (isVoucherPercent(pgg)) {
            BigDecimal percent = pgg.getGiaTriGiamGia();
            if (percent == null || percent.signum() <= 0) return BigDecimal.ZERO;

            if (percent.compareTo(BigDecimal.ZERO) < 0) percent = BigDecimal.ZERO;
            if (percent.compareTo(BigDecimal.valueOf(100)) > 0) percent = BigDecimal.valueOf(100);

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
        HoaDonResponse res = mapper.map(e, HoaDonResponse.class);
        res.setTrangThaiLabel(toTrangThaiLabel(res.getTrangThaiHienTai()));
        return res;
    }
}