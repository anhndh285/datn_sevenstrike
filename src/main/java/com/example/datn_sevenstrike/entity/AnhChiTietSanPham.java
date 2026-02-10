package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "anh_chi_tiet_san_pham")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AnhChiTietSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_chi_tiet_san_pham", nullable = false)
    private Integer idChiTietSanPham;

    @Column(name = "duong_dan_anh", nullable = false, length = 255)
    private String duongDanAnh;

    @Column(name = "la_anh_dai_dien", nullable = false)
    private Boolean laAnhDaiDien;

    @Column(name = "mo_ta", length = 255)
    private String moTa;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chi_tiet_san_pham", insertable = false, updatable = false)
    @ToString.Exclude
    private ChiTietSanPham chiTietSanPham;
}
