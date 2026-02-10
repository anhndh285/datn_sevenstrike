package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dia_chi_khach_hang")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DiaChiKhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_khach_hang", nullable = false)
    private Integer idKhachHang;

    @Column(name = "ma_dia_chi", insertable = false, updatable = false, length = 7)
    private String maDiaChi;

    @Column(name = "ten_dia_chi", nullable = false, length = 255)
    private String tenDiaChi;

    @Column(name = "thanh_pho", length = 255)
    private String thanhPho;

    @Column(name = "quan", length = 255)
    private String quan;

    @Column(name = "phuong", length = 255)
    private String phuong;

    @Column(name = "dia_chi_cu_the", length = 255)
    private String diaChiCuThe;

    @Column(name = "mac_dinh", nullable = false)
    private Boolean macDinh;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang", insertable = false, updatable = false)
    @ToString.Exclude
    private KhachHang khachHang;
}
