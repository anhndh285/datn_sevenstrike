package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "dot_giam_gia")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DotGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_dot_giam_gia", insertable = false, updatable = false, length = 8)
    private String maDotGiamGia;

    @Column(name = "ten_dot_giam_gia", nullable = false, length = 255)
    private String tenDotGiamGia;

    @Column(name = "loai_giam_gia", nullable = false)
    private Boolean loaiGiamGia; // DB chốt: chỉ % => luôn false(0)

    @Column(name = "gia_tri_giam_gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaTriGiamGia;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Column(name = "muc_uu_tien", nullable = false)
    private Integer mucUuTien;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
