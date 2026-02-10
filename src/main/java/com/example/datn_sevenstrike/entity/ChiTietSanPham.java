package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "chi_tiet_san_pham")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ChiTietSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_san_pham", nullable = false)
    private Integer idSanPham;

    @Column(name = "id_mau_sac", nullable = false)
    private Integer idMauSac;

    @Column(name = "id_kich_thuoc", nullable = false)
    private Integer idKichThuoc;

    @Column(name = "id_loai_san", nullable = false)
    private Integer idLoaiSan;

    @Column(name = "id_form_chan", nullable = false)
    private Integer idFormChan;

    @Column(name = "ma_chi_tiet_san_pham", insertable = false, updatable = false, length = 9)
    private String maChiTietSanPham;

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "gia_niem_yet", nullable = false, precision = 18, scale = 2)
    private BigDecimal giaNiemYet;

    @Column(name = "gia_ban", precision = 18, scale = 2)
    private BigDecimal giaBan;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "nguoi_tao")
    private Integer nguoiTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "nguoi_cap_nhat")
    private Integer nguoiCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", insertable = false, updatable = false)
    @ToString.Exclude
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mau_sac", insertable = false, updatable = false)
    @ToString.Exclude
    private MauSac mauSac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kich_thuoc", insertable = false, updatable = false)
    @ToString.Exclude
    private KichThuoc kichThuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_san", insertable = false, updatable = false)
    @ToString.Exclude
    private LoaiSan loaiSan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_form_chan", insertable = false, updatable = false)
    @ToString.Exclude
    private FormChan formChan;
}
