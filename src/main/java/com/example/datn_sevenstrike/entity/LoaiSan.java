package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "loai_san")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoaiSan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_loai_san", nullable = false, insertable = false, updatable = false)
    private String maLoaiSan;

    @Column(name = "ten_loai_san", nullable = true)
    private String tenLoaiSan;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

}
