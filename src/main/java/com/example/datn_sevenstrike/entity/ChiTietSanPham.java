package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "chi_tiet_san_pham")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChiTietSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_san_pham", nullable = true)
    private Integer idSanPham;

    @Column(name = "id_mau_sac", nullable = true)
    private Integer idMauSac;

    @Column(name = "id_kich_thuoc", nullable = true)
    private Integer idKichThuoc;

    @Column(name = "id_loai_san", nullable = true)
    private Integer idLoaiSan;

    @Column(name = "id_form_chan", nullable = true)
    private Integer idFormChan;

    @Column(name = "ma_chi_tiet_san_pham", nullable = false, insertable = false, updatable = false)
    private String maChiTietSanPham;

    @Column(name = "so_luong", nullable = true)
    private Integer soLuong;

    @Column(name = "gia_niem_yet", nullable = true)
    private BigDecimal giaNiemYet;

    @Column(name = "gia_ban", nullable = false)
    private BigDecimal giaBan;

    @Column(name = "trang_thai", nullable = true)
    private Boolean trangThai;

    @Column(name = "ghi_chu", nullable = false)
    private String ghiChu;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

    @Column(name = "ngay_tao", nullable = true)
    private LocalDateTime ngayTao;

    @Column(name = "nguoi_tao", nullable = false)
    private Integer nguoiTao;

    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat;

    @Column(name = "nguoi_cap_nhat", nullable = false)
    private Integer nguoiCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham", insertable = false, updatable = false)
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mau_sac", insertable = false, updatable = false)
    private MauSac mauSac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kich_thuoc", insertable = false, updatable = false)
    private KichThuoc kichThuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_loai_san", insertable = false, updatable = false)
    private LoaiSan loaiSan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_form_chan", insertable = false, updatable = false)
    private FormChan formChan;

}
