package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "kich_thuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KichThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_kich_thuoc", insertable = false, updatable = false, length = 7)
    private String maKichThuoc;

    @Column(name = "ten_kich_thuoc", nullable = false, length = 50)
    private String tenKichThuoc;

    @Column(name = "gia_tri_kich_thuoc", precision = 4, scale = 1)
    private BigDecimal giaTriKichThuoc; // 38.0..45.0

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
