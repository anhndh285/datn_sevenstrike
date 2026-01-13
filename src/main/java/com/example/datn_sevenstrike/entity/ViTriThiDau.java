package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vi_tri_thi_dau")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViTriThiDau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_vi_tri", nullable = false, insertable = false, updatable = false)
    private String maViTri;

    @Column(name = "ten_vi_tri", nullable = true)
    private String tenViTri;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

}
