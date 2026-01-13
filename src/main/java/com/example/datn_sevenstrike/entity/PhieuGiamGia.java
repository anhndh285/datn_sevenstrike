package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "phieu_giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_phieu_giam_gia", nullable = false, insertable = false, updatable = false)
    private String maPhieuGiamGia;

    @Column(name = "ten_phieu_giam_gia", nullable = true)
    private String tenPhieuGiamGia;

    @Column(name = "loai_phieu_giam_gia", nullable = true)
    private Boolean loaiPhieuGiamGia;

    @Column(name = "gia_tri_giam_gia", nullable = true)
    private BigDecimal giaTriGiamGia;

    @Column(name = "so_tien_giam_toi_da", nullable = false)
    private BigDecimal soTienGiamToiDa;

    @Column(name = "hoa_don_toi_thieu", nullable = false)
    private BigDecimal hoaDonToiThieu;

    @Column(name = "so_luong_su_dung", nullable = true)
    private Integer soLuongSuDung;

    @Column(name = "ngay_bat_dau", nullable = true)
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = true)
    private LocalDate ngayKetThuc;

    @Column(name = "trang_thai", nullable = true)
    private Boolean trangThai;

    @Column(name = "mo_ta", nullable = false)
    private String moTa;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

}