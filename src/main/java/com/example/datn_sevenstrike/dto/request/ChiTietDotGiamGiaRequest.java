package com.example.datn_sevenstrike.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietDotGiamGiaRequest {

    private Integer idDotGiamGia;
    private Integer idChiTietSanPham;

    private Integer soLuongApDung;
    private BigDecimal giaTriGiamRieng;
    private BigDecimal soTienGiamToiDaRieng;

    private Boolean trangThai;
    private String ghiChu;

    private Boolean xoaMem;

    private Integer nguoiTao;
    private Integer nguoiCapNhat;
}
