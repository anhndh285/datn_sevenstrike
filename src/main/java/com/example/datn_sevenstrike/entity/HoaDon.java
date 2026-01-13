package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_khach_hang", nullable = false)
    private Integer idKhachHang;

    @Column(name = "id_nhan_vien", nullable = false)
    private Integer idNhanVien;

    @Column(name = "id_phieu_giam_gia", nullable = false)
    private Integer idPhieuGiamGia;

    @Column(name = "id_phieu_giam_gia_ca_nhan", nullable = false)
    private Integer idPhieuGiamGiaCaNhan;

    @Column(name = "ma_hoa_don", nullable = false, insertable = false, updatable = false)
    private String maHoaDon;

    @Column(name = "loai_don", nullable = true)
    private Boolean loaiDon;

    @Column(name = "phi_van_chuyen", nullable = true)
    private BigDecimal phiVanChuyen;

    @Column(name = "tong_tien", nullable = true)
    private BigDecimal tongTien;

    @Column(name = "tong_tien_sau_giam", nullable = true)
    private BigDecimal tongTienSauGiam;

    @Column(name = "tong_tien_giam", nullable = false, insertable = false, updatable = false)
    private BigDecimal tongTienGiam;

    @Column(name = "ten_khach_hang", nullable = true)
    private String tenKhachHang;

    @Column(name = "dia_chi_khach_hang", nullable = true)
    private String diaChiKhachHang;

    @Column(name = "so_dien_thoai_khach_hang", nullable = true)
    private String soDienThoaiKhachHang;

    @Column(name = "email_khach_hang", nullable = false)
    private String emailKhachHang;

    @Column(name = "trang_thai_hien_tai", nullable = true)
    private String trangThaiHienTai;

    @Column(name = "ngay_tao", nullable = true)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_thanh_toan", nullable = false)
    private LocalDateTime ngayThanhToan;

    @Column(name = "ghi_chu", nullable = false)
    private String ghiChu;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

    @Column(name = "nguoi_tao", nullable = false)
    private Integer nguoiTao;

    @Column(name = "ngay_cap_nhat", nullable = false)
    private LocalDateTime ngayCapNhat;

    @Column(name = "nguoi_cap_nhat", nullable = false)
    private Integer nguoiCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang", insertable = false, updatable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien", insertable = false, updatable = false)
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia", insertable = false, updatable = false)
    private PhieuGiamGia phieuGiamGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia_ca_nhan", insertable = false, updatable = false)
    private PhieuGiamGiaCaNhan phieuGiamGiaCaNhan;

}