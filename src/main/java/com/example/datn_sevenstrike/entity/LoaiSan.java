package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "loai_san")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class LoaiSan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_loai_san", insertable = false, updatable = false, length = 7)
    private String maLoaiSan;

    @Column(name = "ten_loai_san", nullable = false, length = 255)
    private String tenLoaiSan;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
