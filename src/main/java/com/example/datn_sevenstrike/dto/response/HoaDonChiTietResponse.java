package com.example.datn_sevenstrike.dto.response;

import java.math.BigDecimal;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HoaDonChiTietResponse {
    private Integer id;
    private Integer idHoaDon;
    private Integer idChiTietSanPham;
    private String maHoaDonChiTiet;
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private String ghiChu;
    private Boolean xoaMem;
}