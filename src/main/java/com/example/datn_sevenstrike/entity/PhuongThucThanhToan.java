package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phuong_thuc_thanh_toan")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PhuongThucThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_phuong_thuc_thanh_toan", insertable = false, updatable = false, length = 9)
    private String maPhuongThucThanhToan;

    @Column(name = "ten_phuong_thuc_thanh_toan", nullable = false, length = 255)
    private String tenPhuongThucThanhToan;

    @Column(name = "nha_cung_cap", length = 50)
    private String nhaCungCap;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
