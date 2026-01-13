package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhan_vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_quyen_han", nullable = true)
    private Integer idQuyenHan;

    @Column(name = "ma_nhan_vien", nullable = false, insertable = false, updatable = false)
    private String maNhanVien;

    @Column(name = "ten_nhan_vien", nullable = true)
    private String tenNhanVien;

    @Column(name = "ten_tai_khoan", nullable = true)
    private String tenTaiKhoan;

    @Column(name = "mat_khau", nullable = true)
    private String matKhau;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "so_dien_thoai", nullable = false)
    private String soDienThoai;

    @Column(name = "anh_nhan_vien", nullable = false)
    private String anhNhanVien;

    @Column(name = "ngay_sinh", nullable = false)
    private LocalDate ngaySinh;

    @Column(name = "ghi_chu", nullable = false)
    private String ghiChu;

    @Column(name = "thanh_pho", nullable = false)
    private String thanhPho;

    @Column(name = "quan", nullable = false)
    private String quan;

    @Column(name = "phuong", nullable = false)
    private String phuong;

    @Column(name = "dia_chi_cu_the", nullable = false)
    private String diaChiCuThe;

    @Column(name = "cccd", nullable = false)
    private String cccd;

    @Column(name = "trang_thai", nullable = true)
    private Boolean trangThai;

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
    @JoinColumn(name = "id_quyen_han", insertable = false, updatable = false)
    private QuyenHan quyenHan;

}
