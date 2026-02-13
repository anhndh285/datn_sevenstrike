package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "giao_ca")
@Getter
@Setter
public class GiaoCa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_giao_ca", insertable = false, updatable = false)
    private String maGiaoCa;

    @Column(name = "id_lich_lam_viec", nullable = false)
    private Integer idLichLamViec;

    @Column(name = "id_nhan_vien", nullable = false)
    private Integer idNhanVien;

    @Column(name = "id_giao_ca_truoc")
    private Integer idGiaoCaTruoc;

    @Column(name = "thoi_gian_nhan_ca", nullable = false)
    private LocalDateTime thoiGianNhanCa;

    @Column(name = "thoi_gian_ket_ca")
    private LocalDateTime thoiGianKetCa;

    @Column(name = "tien_ban_giao_du_kien", nullable = false, precision = 18, scale = 2)
    private BigDecimal tienBanGiaoDuKien = BigDecimal.ZERO;

    @Column(name = "tien_dau_ca_nhap", precision = 18, scale = 2)
    private BigDecimal tienDauCaNhap;

    @Column(name = "da_xac_nhan_tien_dau_ca", nullable = false)
    private Boolean daXacNhanTienDauCa = false;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai = 0;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem = false;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "nguoi_tao")
    private Integer nguoiTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "nguoi_cap_nhat")
    private Integer nguoiCapNhat;
}

