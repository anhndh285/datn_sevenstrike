package com.example.datn_sevenstrike.dto.client;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyVoucherDTO {
    private Integer id;
    private String maPhieuGiamGia;
    private String tenPhieuGiamGia;
    private Boolean loaiPhieuGiamGia;
    private BigDecimal giaTriGiamGia;
    private BigDecimal soTienGiamToiDa;
    private BigDecimal hoaDonToiThieu;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String moTa;

    // Nguồn & trạng thái
    private String nguon;                  // "personal" / "public"
    private Boolean daSuDung;
    private LocalDate ngayNhan;
    private String maPhieuGiamGiaCaNhan;
    private Integer idPhieuGiamGiaCaNhan;

    // Trạng thái hiển thị: "available", "used", "expired", "upcoming"
    private String trangThaiHienThi;
}