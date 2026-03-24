// File: src/main/java/com/example/datn_sevenstrike/service/GiaoCaService.java
package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.GiaoCaRequest;
import com.example.datn_sevenstrike.dto.response.GiaoCaResponse;
import com.example.datn_sevenstrike.entity.GiaoCa;
import com.example.datn_sevenstrike.entity.GiaoDichThanhToan;
import com.example.datn_sevenstrike.entity.LichLamViec;
import com.example.datn_sevenstrike.entity.NhanVien;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.GiaoCaRepository;
import com.example.datn_sevenstrike.repository.GiaoDichThanhToanRepository;
import com.example.datn_sevenstrike.repository.LichLamViecNhanVienRepository;
import com.example.datn_sevenstrike.repository.LichLamViecRepository;
import com.example.datn_sevenstrike.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GiaoCaService {

    private final GiaoCaRepository giaoCaRepo;
    private final GiaoDichThanhToanRepository giaoDichThanhToanRepo;
    private final LichLamViecRepository lichLamViecRepo;
    private final LichLamViecNhanVienRepository lichLamViecNhanVienRepo;
    private final NhanVienRepository nhanVienRepo;

    @Transactional(readOnly = true)
    public Page<GiaoCaResponse> getPage(int pageNo, int pageSize) {
        List<GiaoCaResponse> allRows = new ArrayList<>();

        // 1) Lấy toàn bộ lịch sử giao ca như cũ
        List<GiaoCaResponse> giaoCaRows = giaoCaRepo
                .findAll(Sort.by(Sort.Direction.DESC, "thoiGianNhanCa"))
                .stream()
                .map(this::mapToResponse)
                .toList();

        allRows.addAll(giaoCaRows);

        // 2) Lấy thêm lịch sử admin thu tiền mặt ngoài ca
        List<GiaoCaResponse> adminCashRows = giaoDichThanhToanRepo
                .findAdminCashHistory()
                .stream()
                .map(this::mapAdminCashToResponse)
                .toList();

        allRows.addAll(adminCashRows);

        // 3) Sort chung theo thời gian phát sinh giảm dần
        allRows.sort(
                Comparator.comparing(
                        GiaoCaResponse::getThoiGianPhatSinh,
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
        );

        // 4) Phân trang thủ công sau khi merge 2 nguồn dữ liệu
        int start = pageNo * pageSize;
        int end = Math.min(start + pageSize, allRows.size());

        List<GiaoCaResponse> content =
                start >= allRows.size() ? List.of() : allRows.subList(start, end);

        return new PageImpl<>(content, PageRequest.of(pageNo, pageSize), allRows.size());
    }

    @Transactional(readOnly = true)
    public GiaoCaResponse getCaLamViecHienTai(Integer idNhanVien) {
        GiaoCa gc = giaoCaRepo.findCaDangHoatDong(idNhanVien)
                .orElseThrow(() -> new NotFoundEx("Nhân viên hiện không trong ca làm việc nào."));
        return mapToResponse(gc);
    }

    @Transactional
    public GiaoCaResponse xacNhanTienDauCa(Integer idGiaoCa, BigDecimal tienNhap) {
        GiaoCa gc = giaoCaRepo.findById(idGiaoCa)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ca"));

        if (tienNhap == null || tienNhap.compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestEx("Tiền nhập đầu ca phải lớn hơn hoặc bằng 0!");
        }

        if (gc.getTienBanGiaoDuKien().compareTo(tienNhap) != 0) {
            throw new BadRequestEx("Tiền đầu ca không khớp với số tiền hệ thống dự kiến (" + gc.getTienBanGiaoDuKien() + ")!");
        }

        gc.setTienDauCaNhap(tienNhap);
        gc.setDaXacNhanTienDauCa(true);

        return mapToResponse(giaoCaRepo.save(gc));
    }

    @Transactional
    public GiaoCaResponse batDauCa(GiaoCaRequest req) {
        NhanVien nv = nhanVienRepo.findById(req.getIdNhanVien())
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy nhân viên"));

        if (giaoCaRepo.findCaDangHoatDong(req.getIdNhanVien()).isPresent()) {
            throw new BadRequestEx("Nhân viên đang trong ca làm khác!");
        }

        LichLamViec lich = lichLamViecRepo.findByIdAndXoaMemFalse(req.getIdLichLamViec())
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy lịch làm việc!"));

        boolean isAssigned = lichLamViecNhanVienRepo.existsByLichLamViecAndNhanVien(lich.getId(), nv.getId());
        if (!isAssigned) {
            throw new BadRequestEx("Nhân viên không có trong danh sách phân công của lịch làm việc này!");
        }

        Optional<GiaoCa> catruocOpt = giaoCaRepo.findCaLamViecLienKeTruocDo();
        BigDecimal tienDuKien = BigDecimal.ZERO;
        GiaoCa caTruoc = null;

        if (catruocOpt.isPresent()) {
            caTruoc = catruocOpt.get();
            BigDecimal tienVonCaTruoc = caTruoc.getTienDauCaNhap() != null ? caTruoc.getTienDauCaNhap() : BigDecimal.ZERO;
            BigDecimal doanhThuCaTruoc = giaoCaRepo.tinhDoanhThuCa(caTruoc.getId());
            tienDuKien = tienVonCaTruoc.add(doanhThuCaTruoc);
        }

        GiaoCa giaoCa = new GiaoCa();
        giaoCa.setNhanVien(nv);
        giaoCa.setLichLamViec(lich);
        giaoCa.setGiaoCaTruoc(caTruoc);
        giaoCa.setThoiGianNhanCa(LocalDateTime.now());
        giaoCa.setTienBanGiaoDuKien(tienDuKien);
        giaoCa.setTienDauCaNhap(req.getTienDauCaNhap());

        if (req.getTienDauCaNhap() != null && req.getTienDauCaNhap().compareTo(tienDuKien) == 0) {
            giaoCa.setDaXacNhanTienDauCa(true);
        } else {
            throw new BadRequestEx("Tiền đầu ca không khớp với tiền ca trước");
        }

        giaoCa.setTrangThai(0);
        giaoCa.setXoaMem(false);

        return mapToResponse(giaoCaRepo.save(giaoCa));
    }

    @Transactional
    public GiaoCaResponse ketThucCa(Integer idGiaoCa, GiaoCaRequest req) {
        GiaoCa gc = giaoCaRepo.findById(idGiaoCa)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy ca"));

        if (gc.getTrangThai() == 1) {
            throw new BadRequestEx("Ca này đã được đóng trước đó.");
        }

        if (!Boolean.TRUE.equals(gc.getDaXacNhanTienDauCa())) {
            throw new BadRequestEx("Bạn chưa xác nhận tiền đầu ca, không thể kết thúc ca!");
        }

        gc.setThoiGianKetCa(LocalDateTime.now());
        gc.setTrangThai(1);
        gc.setGhiChu(req.getGhiChu());

        return mapToResponse(giaoCaRepo.save(gc));
    }

    private GiaoCaResponse mapToResponse(GiaoCa entity) {
        GiaoCaResponse res = GiaoCaResponse.builder()
                .id(entity.getId())
                .maGiaoCa(entity.getMaGiaoCa())
                .tienBanGiaoDuKien(entity.getTienBanGiaoDuKien())
                .tienDauCaNhap(entity.getTienDauCaNhap())
                .daXacNhanTienDauCa(entity.getDaXacNhanTienDauCa())
                .thoiGianNhanCa(entity.getThoiGianNhanCa())
                .thoiGianKetCa(entity.getThoiGianKetCa())
                .trangThai(entity.getTrangThai())
                .ghiChu(entity.getGhiChu())
                .ngayTao(entity.getNgayTao())
                .ngayCapNhat(entity.getNgayCapNhat())
                .nguoiTao(entity.getNguoiTao())
                .nguoiCapNhat(entity.getNguoiCapNhat())
                .loaiBanGhi("GIAO_CA")
                .thoiGianPhatSinh(entity.getThoiGianNhanCa() != null ? entity.getThoiGianNhanCa() : entity.getNgayTao())
                .build();

        if (entity.getNhanVien() != null) {
            res.setTenNhanVien(entity.getNhanVien().getTenTaiKhoan());
        }

        if (entity.getLichLamViec() != null && entity.getLichLamViec().getIdCaLam() != null) {
            var ca = entity.getLichLamViec().getIdCaLam();
            res.setTenCaLam(ca.getTenCa());
            res.setGioBatDauCa(ca.getGioBatDau());
            res.setGioKetThucCa(ca.getGioKetThuc());
        }

        try {
            BigDecimal doanhThu = giaoCaRepo.tinhDoanhThuCa(entity.getId());
            res.setTongTienTrongCa(doanhThu != null ? doanhThu : BigDecimal.ZERO);
        } catch (Exception e) {
            res.setTongTienTrongCa(BigDecimal.ZERO);
        }

        try {
            BigDecimal tienMatCa = giaoCaRepo.tinhDoanhThuTienMatCa(entity.getId());
            res.setTienMatTrongCa(tienMatCa != null ? tienMatCa : BigDecimal.ZERO);
        } catch (Exception e) {
            res.setTienMatTrongCa(BigDecimal.ZERO);
        }

        try {
            BigDecimal tienCkCa = giaoCaRepo.tinhDoanhThuChuyenKhoanCa(entity.getId());
            res.setTienChuyenKhoanTrongCa(tienCkCa != null ? tienCkCa : BigDecimal.ZERO);
        } catch (Exception e) {
            res.setTienChuyenKhoanTrongCa(BigDecimal.ZERO);
        }

        return res;
    }

    private GiaoCaResponse mapAdminCashToResponse(GiaoDichThanhToan gd) {
        BigDecimal soTien = gd.getSoTien() != null ? gd.getSoTien() : BigDecimal.ZERO;

        String tenNguoiThu = "Admin";
        if (gd.getNhanVienCapNhat() != null && gd.getNhanVienCapNhat().getTenNhanVien() != null) {
            tenNguoiThu = gd.getNhanVienCapNhat().getTenTaiKhoan();
        }

        String maHoaDon = null;
        if (gd.getHoaDon() != null) {
            maHoaDon = gd.getHoaDon().getMaHoaDon();
        }

        return GiaoCaResponse.builder()
                .id(gd.getId())
                .maGiaoCa(null)
                .tienBanGiaoDuKien(BigDecimal.ZERO)
                .tienDauCaNhap(BigDecimal.ZERO)
                .daXacNhanTienDauCa(true)
                .thoiGianNhanCa(gd.getThoiGianTao())
                .thoiGianKetCa(null)
                .trangThai(1)
                .ghiChu("Admin thu tiền mặt" + (maHoaDon != null ? " - " + maHoaDon : ""))
                .ngayTao(gd.getThoiGianTao())
                .ngayCapNhat(gd.getThoiGianCapNhat())
                .nguoiTao(null)
                .nguoiCapNhat(gd.getNguoiCapNhat())

                .tenNhanVien(tenNguoiThu)
                .tenCaLam("ADMIN")
                .gioBatDauCa(null)
                .gioKetThucCa(null)

                .tongTienTrongCa(soTien)
                .tienMatTrongCa(soTien)
                .tienChuyenKhoanTrongCa(BigDecimal.ZERO)

                .loaiBanGhi("ADMIN_CASH")
                .maHoaDon(maHoaDon)
                .thoiGianPhatSinh(gd.getThoiGianTao())
                .build();
    }
}