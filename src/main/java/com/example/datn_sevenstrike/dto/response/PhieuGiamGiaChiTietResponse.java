package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGiaChiTietResponse {

    private Integer id;

    private Integer idPhieuGiamGia;
    private Integer idKhachHang;

    private Boolean xoaMem;

    // tiện demo
    private String maKhachHang;
    private String tenKhachHang;

    private String maPhieuGiamGia;
    private String tenPhieuGiamGia;
}
