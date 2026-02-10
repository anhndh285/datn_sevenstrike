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

    @Column(name = "ma_phieu_giam_gia", insertable = false, updatable = false, length = 8)
    private String maPhieuGiamGia;

    @Column(name = "ten_phieu_giam_gia", nullable = false, length = 255)
    private String tenPhieuGiamGia;

    @Column(name = "loai_phieu_giam_gia", nullable = false)
    private Boolean loaiPhieuGiamGia;

    @Column(name = "gia_tri_giam_gia", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaTriGiamGia;

    @Column(name = "so_tien_giam_toi_da", precision = 18, scale = 2)
    private BigDecimal soTienGiamToiDa;

    @Column(name = "hoa_don_toi_thieu", precision = 18, scale = 2)
    private BigDecimal hoaDonToiThieu;

    @Column(name = "so_luong_su_dung", nullable = false)
    private Integer soLuongSuDung;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "mo_ta", length = 255)
    private String moTa;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
