package com.example.datn_sevenstrike.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhieuGiamGiaRequest {
    private String tenPhieuGiamGia;
    private Integer loaiPhieuGiamGia; // true: Cá nhân, false: Công khai
    private Boolean hinhThucGiam;     // true: %, false: VNĐ (Mới thêm)
    private BigDecimal giaTriGiamGia;
    private BigDecimal soTienGiamToiDa;
    private BigDecimal hoaDonToiThieu;
    private Integer soLuongSuDung;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private Integer trangThai;
    private String moTa;
    private Boolean xoaMem;

    // Nếu là phiếu cá nhân, gửi kèm danh sách ID khách hàng luôn
    private List<Long> idKhachHangs;
}