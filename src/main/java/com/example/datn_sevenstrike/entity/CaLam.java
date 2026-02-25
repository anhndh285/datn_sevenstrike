package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "ca_lam")
public class CaLam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 7)
    @Column(name = "ma_ca", length = 7, insertable = false, updatable = false)
    private String maCa;

    @Size(max = 100)
    @NotNull
    @Nationalized
    @Column(name = "ten_ca", nullable = false, length = 100)
    private String tenCa;

    @NotNull
    @Column(name = "gio_bat_dau", nullable = false)
    private LocalTime gioBatDau;

    @NotNull
    @Column(name = "gio_ket_thuc", nullable = false)
    private LocalTime gioKetThuc;

    @Size(max = 255)
    @Nationalized
    @Column(name = "mo_ta")
    private String moTa;

    @NotNull
    @ColumnDefault("1")
    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = false;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem = false;

}