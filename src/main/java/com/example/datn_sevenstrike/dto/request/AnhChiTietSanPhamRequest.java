package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnhChiTietSanPhamRequest {
    private Integer idChiTietSanPham;
    private String duongDanAnh;
    private Boolean laAnhDaiDien;
    private String moTa;
    private Boolean xoaMem;
}
