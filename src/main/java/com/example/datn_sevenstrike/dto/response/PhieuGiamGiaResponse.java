package com.example.datn_sevenstrike.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhieuGiamGiaResponse {
    private Integer id;
    private String maPhieuGiamGia;
    private String tenPhieuGiamGia;
    private Integer loaiPhieuGiamGia;
    private BigDecimal giaTriGiamGia;
    private BigDecimal soTienGiamToiDa;
    private BigDecimal hoaDonToiThieu;
    private Integer soLuongSuDung;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private Integer trangThai;
    private String moTa;
    private Boolean xoaMem;
}