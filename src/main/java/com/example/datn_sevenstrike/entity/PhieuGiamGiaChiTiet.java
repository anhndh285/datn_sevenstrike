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

    @ManyToOne
    @JoinColumn(name = "id_phieu_giam_gia")
    private PhieuGiamGia phieuGiamGia;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang; // Lưu ý: Đảm bảo bạn đã có class KhachHang.java
}