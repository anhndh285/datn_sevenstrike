package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "chi_tiet_dot_giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietDotGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_dot_giam_gia", nullable = true)
    private Integer idDotGiamGia;

    @Column(name = "id_chi_tiet_san_pham", nullable = true)
    private Integer idChiTietSanPham;

    @Column(name = "so_luong_ap_dung", nullable = false)
    private Integer soLuongApDung;

    @Column(name = "gia_tri_giam_rieng", nullable = false)
    private BigDecimal giaTriGiamRieng;

    @Column(name = "so_tien_giam_toi_da_rieng", nullable = false)
    private BigDecimal soTienGiamToiDaRieng;

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
    @JoinColumn(name = "id_dot_giam_gia", insertable = false, updatable = false)
    private DotGiamGia dotGiamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chi_tiet_san_pham", insertable = false, updatable = false)
    private ChiTietSanPham chiTietSanPham;

}
