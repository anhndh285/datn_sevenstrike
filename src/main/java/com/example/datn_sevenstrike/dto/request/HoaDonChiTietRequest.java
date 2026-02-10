package com.example.datn_sevenstrike.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonChiTietRequest {

    private Integer idHoaDon;
    private Integer idChiTietSanPham;

    private Integer soLuong;
    private BigDecimal donGia;

    private String ghiChu;
    private Boolean xoaMem;
}
