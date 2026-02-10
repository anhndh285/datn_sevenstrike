package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "chi_tiet_dot_giam_gia")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ChiTietDotGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_dot_giam_gia", nullable = false)
    private Integer idDotGiamGia;

    @Column(name = "id_chi_tiet_san_pham", nullable = false)
    private Integer idChiTietSanPham;

    @Column(name = "so_luong_ap_dung")
    private Integer soLuongApDung;

    @Column(name = "gia_tri_giam_rieng", precision = 18, scale = 2)
    private BigDecimal giaTriGiamRieng;

    @Column(name = "so_tien_giam_toi_da_rieng", precision = 18, scale = 2)
    private BigDecimal soTienGiamToiDaRieng;

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
    @JoinColumn(name = "id_dot_giam_gia", insertable = false, updatable = false)
    @ToString.Exclude
    private DotGiamGia dotGiamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chi_tiet_san_pham", insertable = false, updatable = false)
    @ToString.Exclude
    private ChiTietSanPham chiTietSanPham;
}
