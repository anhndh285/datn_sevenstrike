package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGiaCaNhanResponse {

    private Integer id;

    private Integer idKhachHang;
    private Integer idPhieuGiamGia;

    private String maPhieuGiamGiaCaNhan;

    private LocalDate ngayNhan;
    private Boolean daSuDung;

    private Boolean xoaMem;

    // tiện demo
    private String maKhachHang;
    private String tenKhachHang;

    private String maPhieuGiamGia;
    private String tenPhieuGiamGia;
}
