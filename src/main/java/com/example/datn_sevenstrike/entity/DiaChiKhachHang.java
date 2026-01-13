package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dia_chi_khach_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaChiKhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_khach_hang", nullable = true)
    private Integer idKhachHang;

    @Column(name = "ma_dia_chi", nullable = false, insertable = false, updatable = false)
    private String maDiaChi;

    @Column(name = "ten_dia_chi", nullable = true)
    private String tenDiaChi;

    @Column(name = "thanh_pho", nullable = false)
    private String thanhPho;

    @Column(name = "quan", nullable = false)
    private String quan;

    @Column(name = "phuong", nullable = false)
    private String phuong;

    @Column(name = "dia_chi_cu_the", nullable = false)
    private String diaChiCuThe;

    @Column(name = "mac_dinh", nullable = true)
    private Boolean macDinh;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang", insertable = false, updatable = false)
    private KhachHang khachHang;

}