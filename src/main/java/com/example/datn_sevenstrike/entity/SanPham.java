package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "san_pham")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_thuong_hieu", nullable = true)
    private Integer idThuongHieu;

    @Column(name = "id_xuat_xu", nullable = false)
    private Integer idXuatXu;

    @Column(name = "id_vi_tri_thi_dau", nullable = false)
    private Integer idViTriThiDau;

    @Column(name = "id_phong_cach_choi", nullable = false)
    private Integer idPhongCachChoi;

    @Column(name = "id_co_giay", nullable = false)
    private Integer idCoGiay;

    @Column(name = "id_chat_lieu", nullable = false)
    private Integer idChatLieu;

    @Column(name = "ma_san_pham", nullable = false, insertable = false, updatable = false)
    private String maSanPham;

    @Column(name = "ten_san_pham", nullable = true)
    private String tenSanPham;

    @Column(name = "mo_ta_ngan", nullable = false)
    private String moTaNgan;

    @Lob
    @Column(name = "mo_ta_chi_tiet", nullable = false)
    private String moTaChiTiet;

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
    @JoinColumn(name = "id_thuong_hieu", insertable = false, updatable = false)
    private ThuongHieu thuongHieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_xuat_xu", insertable = false, updatable = false)
    private XuatXu xuatXu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vi_tri_thi_dau", insertable = false, updatable = false)
    private ViTriThiDau viTriThiDau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phong_cach_choi", insertable = false, updatable = false)
    private PhongCachChoi phongCachChoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_co_giay", insertable = false, updatable = false)
    private CoGiay coGiay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat_lieu", insertable = false, updatable = false)
    private ChatLieu chatLieu;

}
