package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaChiKhachHangRequest {

    private Integer idKhachHang;

    private String tenDiaChi;
    private String thanhPho;
    private String quan;
    private String phuong;
    private String diaChiCuThe;

    private Boolean macDinh;
    private Boolean xoaMem;
}
