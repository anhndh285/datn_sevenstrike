package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ca_lam")
@Getter
@Setter
public class CaLam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_ca", insertable = false, updatable = false)
    private String maCa;

    @Column(name = "ten_ca", nullable = false, length = 100)
    private String tenCa;

    @Column(name = "gio_bat_dau", nullable = false)
    private LocalTime gioBatDau;

    @Column(name = "gio_ket_thuc", nullable = false)
    private LocalTime gioKetThuc;

    @Column(name = "mo_ta", length = 255)
    private String moTa;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = true;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem = false;
}

