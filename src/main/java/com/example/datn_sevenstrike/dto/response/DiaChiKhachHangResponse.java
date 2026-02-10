package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaChiKhachHangResponse {

    private Integer id;
    private Integer idKhachHang;

    private String maDiaChi;
    private String tenDiaChi;

    private String thanhPho;
    private String quan;
    private String phuong;
    private String diaChiCuThe;

    private Boolean macDinh;
    private Boolean xoaMem;

    // tiện demo
    private String maKhachHang;
    private String tenKhachHang;
}
