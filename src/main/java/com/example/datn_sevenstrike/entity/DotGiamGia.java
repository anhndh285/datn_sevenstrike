package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "dot_giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DotGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_dot_giam_gia", nullable = false, insertable = false, updatable = false)
    private String maDotGiamGia;

    @Column(name = "ten_dot_giam_gia", nullable = true)
    private String tenDotGiamGia;

    @Column(name = "loai_giam_gia", nullable = true)
    private Boolean loaiGiamGia;

    @Column(name = "gia_tri_giam_gia", nullable = true)
    private BigDecimal giaTriGiamGia;

    @Column(name = "so_tien_giam_toi_da", nullable = false)
    private BigDecimal soTienGiamToiDa;

    @Column(name = "ngay_bat_dau", nullable = true)
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = true)
    private LocalDate ngayKetThuc;

    @Column(name = "muc_uu_tien", nullable = true)
    private Integer mucUuTien;

    @Column(name = "trang_thai", nullable = true)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

}
