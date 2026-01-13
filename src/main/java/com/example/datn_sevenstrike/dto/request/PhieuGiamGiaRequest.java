package com.example.datn_sevenstrike.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhieuGiamGiaRequest {
    private String tenPhieuGiamGia;
    private Boolean loaiPhieuGiamGia;
    private BigDecimal giaTriGiamGia;
    private BigDecimal soTienGiamToiDa;
    private BigDecimal hoaDonToiThieu;
    private Integer soLuongSuDung;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private Boolean trangThai;
    private String moTa;
    private Boolean xoaMem;
}