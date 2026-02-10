package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "nhan_vien")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_quyen_han", nullable = false)
    private Integer idQuyenHan;

    @Column(name = "ma_nhan_vien", insertable = false, updatable = false, length = 7)
    private String maNhanVien;

    @Column(name = "ten_nhan_vien", nullable = false, length = 255)
    private String tenNhanVien;

    @Column(name = "ten_tai_khoan", nullable = false, length = 255)
    private String tenTaiKhoan;

    @Column(name = "mat_khau", nullable = false, length = 255)
    private String matKhau;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "so_dien_thoai", length = 12)
    private String soDienThoai;

    @Column(name = "anh_nhan_vien", length = 255)
    private String anhNhanVien;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "thanh_pho", length = 255)
    private String thanhPho;

    @Column(name = "quan", length = 255)
    private String quan;

    @Column(name = "phuong", length = 255)
    private String phuong;

    @Column(name = "dia_chi_cu_the", length = 255)
    private String diaChiCuThe;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

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
    @JoinColumn(name = "id_quyen_han", insertable = false, updatable = false)
    @ToString.Exclude
    private QuyenHan quyenHan;
}
