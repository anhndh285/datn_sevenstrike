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

    private BigDecimal thanhTien; // computed

    private String ghiChu;
    private Boolean xoaMem;

    // tiện demo
    private String maHoaDon;

    private String maChiTietSanPham;
    private String maSanPham;
    private String tenSanPham;

    // ✅ thêm thông tin biến thể để FE hiển thị rõ
    private String mauSac;
    private String kichCo;
    private String loaiSan;
    private String formChan;

    private String duongDanAnhDaiDien;
}
