package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "san_pham")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_thuong_hieu", nullable = false)
    private Integer idThuongHieu;

    @Column(name = "id_xuat_xu")
    private Integer idXuatXu;

    @Column(name = "id_vi_tri_thi_dau")
    private Integer idViTriThiDau;

    @Column(name = "id_phong_cach_choi")
    private Integer idPhongCachChoi;

    @Column(name = "id_co_giay")
    private Integer idCoGiay;

    @Column(name = "id_chat_lieu")
    private Integer idChatLieu;

    @Column(name = "ma_san_pham", insertable = false, updatable = false, length = 7)
    private String maSanPham;

    @Column(name = "ten_san_pham", nullable = false, length = 255)
    private String tenSanPham;

    @Column(name = "mo_ta_ngan", length = 500)
    private String moTaNgan;

    @Column(name = "mo_ta_chi_tiet")
    private String moTaChiTiet;

    @Column(name = "trang_thai_kinh_doanh", nullable = false)
    private Boolean trangThaiKinhDoanh;

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
    @JoinColumn(name = "id_thuong_hieu", insertable = false, updatable = false)
    @ToString.Exclude
    private ThuongHieu thuongHieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_xuat_xu", insertable = false, updatable = false)
    @ToString.Exclude
    private XuatXu xuatXu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vi_tri_thi_dau", insertable = false, updatable = false)
    @ToString.Exclude
    private ViTriThiDau viTriThiDau;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phong_cach_choi", insertable = false, updatable = false)
    @ToString.Exclude
    private PhongCachChoi phongCachChoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_co_giay", insertable = false, updatable = false)
    @ToString.Exclude
    private CoGiay coGiay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat_lieu", insertable = false, updatable = false)
    @ToString.Exclude
    private ChatLieu chatLieu;
}
