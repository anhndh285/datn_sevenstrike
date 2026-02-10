package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "phieu_giam_gia_chi_tiet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGiaChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ✅ CHỈ DÙNG QUAN HỆ (KHÔNG DÙNG idPhieuGiamGia/idKhachHang dạng Integer nữa)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_phieu_giam_gia", nullable = false)
    @ToString.Exclude
    private PhieuGiamGia phieuGiamGia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_khach_hang", nullable = false)
    @ToString.Exclude
    private KhachHang khachHang;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
