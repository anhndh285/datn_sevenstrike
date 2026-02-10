package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quyen_han")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class QuyenHan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_quyen_han", insertable = false, updatable = false, length = 7)
    private String maQuyenHan;

    @Column(name = "ten_quyen_han", nullable = false, length = 255)
    private String tenQuyenHan;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
