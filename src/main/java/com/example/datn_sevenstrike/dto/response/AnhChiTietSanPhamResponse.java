package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnhChiTietSanPhamResponse {

    private Integer id;

    private Integer idChiTietSanPham;
    private String maChiTietSanPham;

    private String duongDanAnh;
    private Boolean laAnhDaiDien;

    private String moTa;
    private Boolean xoaMem;
}
