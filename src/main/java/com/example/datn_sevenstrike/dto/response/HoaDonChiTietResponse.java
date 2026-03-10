// File: src/main/java/com/example/datn_sevenstrike/dto/response/HoaDonChiTietResponse.java
package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonChiTietResponse {

    private Integer id;

    private Integer idHoaDon;
    private Integer idChiTietSanPham;

    private String maHoaDonChiTiet;

    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal donGiaCu; // giá hiện tại (real-time) để FE so sánh

    private BigDecimal thanhTien;

    private String ghiChu;
    private Boolean xoaMem;

    private String maHoaDon;

    private String maChiTietSanPham;
    private String maSanPham;
    private String tenSanPham;

    private String mauSac;
    private String kichCo;
    private String loaiSan;
    private String formChan;

    private String duongDanAnhDaiDien;
    private Integer tonKho; // tồn kho hiện tại của chi_tiet_san_pham
}