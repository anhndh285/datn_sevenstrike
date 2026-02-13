package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.constants.TrangThaiGiaoCa;
import com.example.datn_sevenstrike.dto.request.DongCaRequest;
import com.example.datn_sevenstrike.dto.request.GiaoCaVaoCaRequest;
import com.example.datn_sevenstrike.dto.request.XacNhanTienDauCaRequest;
import com.example.datn_sevenstrike.dto.response.GiaoCaResponse;
import com.example.datn_sevenstrike.entity.GiaoCa;
import com.example.datn_sevenstrike.entity.LichLamViec;
import com.example.datn_sevenstrike.exception.NgoaiLeDuLieuKhongHopLe;
import com.example.datn_sevenstrike.exception.NgoaiLeKhongTimThay;
import com.example.datn_sevenstrike.repository.GiaoCaRepository;
import com.example.datn_sevenstrike.repository.LichLamViecNhanVienRepository;
import com.example.datn_sevenstrike.repository.LichLamViecRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GiaoCaService {

    private final GiaoCaRepository giaoCaRepo;
    private final LichLamViecRepository lichRepo;
    private final LichLamViecNhanVienRepository llvnvRepo;

    public GiaoCaResponse dangHoatDong(Integer idNhanVien) {
        return giaoCaRepo
                .findFirstByIdNhanVienAndXoaMemFalseAndTrangThaiAndThoiGianKetCaIsNullOrderByIdDesc(
                        idNhanVien, TrangThaiGiaoCa.DANG_HOAT_DONG.code
                )
                .map(this::toResponse)
                .orElse(null);
    }

    @Transactional
    public GiaoCaResponse vaoCa(GiaoCaVaoCaRequest req) {
        LichLamViec llv = lichRepo.findByIdAndXoaMemFalse(req.getIdLichLamViec())
                .orElseThrow(() -> new NgoaiLeKhongTimThay("Không tìm thấy lịch làm việc."));

        // case khó: chỉ cho vào ca của ngày hôm nay (đỡ nhầm)
        if (!LocalDate.now().equals(llv.getNgayLam())) {
            throw new NgoaiLeDuLieuKhongHopLe("Chỉ được vào ca của ngày hôm nay.");
        }

        // check NV có nằm trong lịch (DB trigger cũng chặn, nhưng BE trả message đẹp)
        boolean coTrongCa = llvnvRepo.existsByIdLichLamViecAndIdNhanVienAndXoaMemFalse(
                req.getIdLichLamViec(), req.getIdNhanVien()
        );
        if (!coTrongCa) {
            throw new NgoaiLeDuLieuKhongHopLe("Nhân viên không nằm trong ca làm nên không thể vào ca.");
        }

        // mỗi NV chỉ có 1 ca đang mở
        if (giaoCaRepo.existsByIdNhanVienAndXoaMemFalseAndTrangThaiAndThoiGianKetCaIsNull(
                req.getIdNhanVien(), TrangThaiGiaoCa.DANG_HOAT_DONG.code
        )) {
            throw new NgoaiLeDuLieuKhongHopLe("Nhân viên đang có ca hoạt động, không thể vào ca mới.");
        }

        BigDecimal tienDuKien = req.getTienBanGiaoDuKien() == null ? BigDecimal.ZERO : req.getTienBanGiaoDuKien();
        if (tienDuKien.compareTo(BigDecimal.ZERO) < 0) {
            throw new NgoaiLeDuLieuKhongHopLe("Tiền bàn giao dự kiến không hợp lệ.");
        }

        // lấy giao ca trước (nếu có) theo cùng lịch làm việc, trạng thái đã đóng
        Integer idGiaoCaTruoc = giaoCaRepo.findFirstByIdLichLamViecAndXoaMemFalseAndTrangThaiOrderByIdDesc(
                req.getIdLichLamViec(), TrangThaiGiaoCa.DA_DONG_CA.code
        )
                .map(GiaoCa::getId)
                .orElse(null);

        GiaoCa gc = new GiaoCa();
        gc.setIdLichLamViec(req.getIdLichLamViec());
        gc.setIdNhanVien(req.getIdNhanVien());
        gc.setIdGiaoCaTruoc(idGiaoCaTruoc);
        gc.setThoiGianNhanCa(LocalDateTime.now());
        gc.setThoiGianKetCa(null);
        gc.setTienBanGiaoDuKien(tienDuKien);
        gc.setTienDauCaNhap(null);
        gc.setDaXacNhanTienDauCa(false);
        gc.setTrangThai(TrangThaiGiaoCa.DANG_HOAT_DONG.code);
        gc.setXoaMem(false);

        return toResponse(giaoCaRepo.save(gc));
    }

    @Transactional
    public GiaoCaResponse xacNhanTienDauCa(XacNhanTienDauCaRequest req) {
        GiaoCa gc = giaoCaRepo.findByIdAndXoaMemFalse(req.getIdGiaoCa())
                .orElseThrow(() -> new NgoaiLeKhongTimThay("Không tìm thấy giao ca."));

        // ✅ dùng codeEquals để check trạng thái
        if (!TrangThaiGiaoCa.DANG_HOAT_DONG.codeEquals(gc.getTrangThai()) || gc.getThoiGianKetCa() != null) {
            throw new NgoaiLeDuLieuKhongHopLe("Ca này không còn hoạt động.");
        }

        // optional: check đúng nhân viên
        if (req.getIdNhanVien() != null && !req.getIdNhanVien().equals(gc.getIdNhanVien())) {
            throw new NgoaiLeDuLieuKhongHopLe("Bạn không có quyền xác nhận tiền đầu ca của ca này.");
        }

        BigDecimal tienNhap = req.getTienDauCaNhap();
        if (tienNhap.compareTo(BigDecimal.ZERO) < 0) {
            throw new NgoaiLeDuLieuKhongHopLe("Tiền đầu ca nhập không hợp lệ.");
        }

        if (tienNhap.compareTo(gc.getTienBanGiaoDuKien()) != 0) {
            throw new NgoaiLeDuLieuKhongHopLe("Tiền đầu ca không khớp số tiền bàn giao dự kiến.");
        }

        gc.setTienDauCaNhap(tienNhap);
        gc.setDaXacNhanTienDauCa(true);
        gc.setNgayCapNhat(LocalDateTime.now());

        return toResponse(giaoCaRepo.save(gc));
    }

    @Transactional
    public GiaoCaResponse dongCa(DongCaRequest req) {
        GiaoCa gc = giaoCaRepo.findByIdAndXoaMemFalse(req.getIdGiaoCa())
                .orElseThrow(() -> new NgoaiLeKhongTimThay("Không tìm thấy giao ca."));

        if (!TrangThaiGiaoCa.DANG_HOAT_DONG.codeEquals(gc.getTrangThai()) || gc.getThoiGianKetCa() != null) {
            throw new NgoaiLeDuLieuKhongHopLe("Ca này không còn hoạt động.");
        }

        // case khó: muốn làm chặt thì yêu cầu xác nhận tiền đầu ca trước khi đóng ca
        if (!Boolean.TRUE.equals(gc.getDaXacNhanTienDauCa())) {
            throw new NgoaiLeDuLieuKhongHopLe("Bạn chưa xác nhận tiền đầu ca nên không thể đóng ca.");
        }

        gc.setTrangThai(TrangThaiGiaoCa.DA_DONG_CA.code);
        gc.setThoiGianKetCa(LocalDateTime.now());
        gc.setGhiChu(req.getGhiChu());
        gc.setNgayCapNhat(LocalDateTime.now());

        return toResponse(giaoCaRepo.save(gc));
    }

    private GiaoCaResponse toResponse(GiaoCa e) {
        return GiaoCaResponse.builder()
                .id(e.getId())
                .maGiaoCa(e.getMaGiaoCa())
                .idLichLamViec(e.getIdLichLamViec())
                .idNhanVien(e.getIdNhanVien())
                .idGiaoCaTruoc(e.getIdGiaoCaTruoc())
                .thoiGianNhanCa(e.getThoiGianNhanCa())
                .thoiGianKetCa(e.getThoiGianKetCa())
                .tienBanGiaoDuKien(e.getTienBanGiaoDuKien())
                .tienDauCaNhap(e.getTienDauCaNhap())
                .daXacNhanTienDauCa(e.getDaXacNhanTienDauCa())
                .trangThai(e.getTrangThai())
                .ghiChu(e.getGhiChu())
                .build();
    }
}
