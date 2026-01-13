package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "hoa_don_chi_tiet")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HoaDonChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_hoa_don", nullable = true)
    private Integer idHoaDon;

    @Column(name = "id_chi_tiet_san_pham", nullable = true)
    private Integer idChiTietSanPham;

    @Column(name = "ma_hoa_don_chi_tiet", nullable = false, insertable = false, updatable = false)
    private String maHoaDonChiTiet;

    @Column(name = "so_luong", nullable = true)
    private Integer soLuong;

    @Column(name = "don_gia", nullable = true)
    private BigDecimal donGia;

    @Column(name = "thanh_tien", nullable = false, insertable = false, updatable = false)
    private BigDecimal thanhTien;

    @Column(name = "ghi_chu", nullable = false)
    private String ghiChu;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don", insertable = false, updatable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chi_tiet_san_pham", insertable = false, updatable = false)
    private ChiTietSanPham chiTietSanPham;

}