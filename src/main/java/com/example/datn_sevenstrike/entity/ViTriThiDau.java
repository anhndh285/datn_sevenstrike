package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vi_tri_thi_dau")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ViTriThiDau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_vi_tri", insertable = false, updatable = false, length = 7)
    private String maViTri;

    @Column(name = "ten_vi_tri", nullable = false, length = 255)
    private String tenViTri;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
