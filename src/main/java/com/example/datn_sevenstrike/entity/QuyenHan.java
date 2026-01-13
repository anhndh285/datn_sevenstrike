package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quyen_han")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuyenHan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_quyen_han", nullable = false, insertable = false, updatable = false)
    private String maQuyenHan;

    @Column(name = "ten_quyen_han", nullable = true)
    private String tenQuyenHan;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

}
