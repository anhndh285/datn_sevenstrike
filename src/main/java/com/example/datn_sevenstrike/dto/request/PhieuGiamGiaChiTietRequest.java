package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGiaChiTietRequest {

    private Integer idPhieuGiamGia;
    private Integer idKhachHang;

    private Boolean xoaMem;
}
