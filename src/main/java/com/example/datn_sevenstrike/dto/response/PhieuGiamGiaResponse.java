package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGiaResponse {

    private Integer id;
    private String maPhieuGiamGia;

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
