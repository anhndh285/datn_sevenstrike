package com.example.datn_sevenstrike.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGiaCaNhanRequest {

    private Integer idKhachHang;
    private Integer idPhieuGiamGia;

    private LocalDate ngayNhan;
    private Boolean daSuDung;

    private Boolean xoaMem;
}
