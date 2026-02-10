package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "hoa_don_chi_tiet")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class HoaDonChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_hoa_don", nullable = false)
    private Integer idHoaDon;

    @Column(name = "id_chi_tiet_san_pham", nullable = false)
    private Integer idChiTietSanPham;

    @Column(name = "ma_hoa_don_chi_tiet", insertable = false, updatable = false, length = 9)
    private String maHoaDonChiTiet;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal donGia;

    @Column(name = "thanh_tien", insertable = false, updatable = false, precision = 18, scale = 2)
    private BigDecimal thanhTien; // computed

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don", insertable = false, updatable = false)
    @ToString.Exclude
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chi_tiet_san_pham", insertable = false, updatable = false)
    @ToString.Exclude
    private ChiTietSanPham chiTietSanPham;
}
