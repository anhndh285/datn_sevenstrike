package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "co_giay")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CoGiay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_co_giay", insertable = false, updatable = false, length = 7)
    private String maCoGiay;

    @Column(name = "ten_co_giay", nullable = false, length = 255)
    private String tenCoGiay;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
