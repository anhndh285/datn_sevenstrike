package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mau_sac")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MauSac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_mau_sac", insertable = false, updatable = false, length = 7)
    private String maMauSac;

    @Column(name = "ten_mau_sac", nullable = false, length = 255)
    private String tenMauSac;

    @Column(name = "ma_mau_hex", length = 7)
    private String maMauHex; // #RRGGBB

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
