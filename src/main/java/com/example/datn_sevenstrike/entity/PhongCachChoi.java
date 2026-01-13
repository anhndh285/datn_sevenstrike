package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phong_cach_choi")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhongCachChoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_phong_cach", nullable = false, insertable = false, updatable = false)
    private String maPhongCach;

    @Column(name = "ten_phong_cach", nullable = true)
    private String tenPhongCach;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

}
