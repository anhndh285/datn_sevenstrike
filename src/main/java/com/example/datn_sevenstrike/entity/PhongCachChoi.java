package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phong_cach_choi")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PhongCachChoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_phong_cach", insertable = false, updatable = false, length = 7)
    private String maPhongCach;

    @Column(name = "ten_phong_cach", nullable = false, length = 255)
    private String tenPhongCach;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
