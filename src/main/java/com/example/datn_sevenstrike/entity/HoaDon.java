package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_khach_hang")
    private Integer idKhachHang;

    @Column(name = "id_nhan_vien")
    private Integer idNhanVien;

    @Column(name = "id_phieu_giam_gia")
    private Integer idPhieuGiamGia;

    @Column(name = "id_phieu_giam_gia_ca_nhan")
    private Integer idPhieuGiamGiaCaNhan;

    @Column(name = "ma_hoa_don", insertable = false, updatable = false, length = 7)
    private String maHoaDon;

    // 0: tại quầy | 1: giao hàng/online
    @Column(name = "loai_don", nullable = false)
    private Boolean loaiDon;

    @Column(name = "phi_van_chuyen", nullable = false, precision = 18, scale = 2)
    private BigDecimal phiVanChuyen;

    @Column(name = "tong_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "tong_tien_sau_giam", nullable = false, precision = 18, scale = 2)
    private BigDecimal tongTienSauGiam;

    @Column(name = "tong_tien_giam", insertable = false, updatable = false, precision = 18, scale = 2)
    private BigDecimal tongTienGiam;

    @Column(name = "ten_khach_hang", nullable = false, length = 255)
    private String tenKhachHang;

    @Column(name = "dia_chi_khach_hang", nullable = false, length = 255)
    private String diaChiKhachHang;

    @Column(name = "so_dien_thoai_khach_hang", nullable = false, length = 12)
    private String soDienThoaiKhachHang;

    @Column(name = "email_khach_hang", length = 255)
    private String emailKhachHang;

    // CHỐT: int code (1..7)
    @Column(name = "trang_thai_hien_tai", nullable = false)
    private Integer trangThaiHienTai;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;

    @Column(name = "nguoi_tao")
    private Integer nguoiTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "nguoi_cap_nhat")
    private Integer nguoiCapNhat;
}
