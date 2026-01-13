package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "khach_hang")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_khach_hang", nullable = false, insertable = false, updatable = false)
    private String maKhachHang;

    @Column(name = "ten_khach_hang", nullable = true)
    private String tenKhachHang;

    @Column(name = "ten_tai_khoan", nullable = true)
    private String tenTaiKhoan;

    @Column(name = "mat_khau", nullable = true)
    private String matKhau;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "so_dien_thoai", nullable = false)
    private String soDienThoai;

    @Column(name = "gioi_tinh", nullable = false)
    private Boolean gioiTinh;

    @Column(name = "ngay_sinh", nullable = false)
    private LocalDate ngaySinh;

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

}