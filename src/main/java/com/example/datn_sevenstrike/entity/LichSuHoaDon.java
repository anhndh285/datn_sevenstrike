package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_hoa_don")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuHoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_hoa_don", nullable = false)
    private Integer idHoaDon;

    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;

    @Column(name = "thoi_gian", insertable = false, updatable = false)
    private LocalDateTime thoiGian;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;

    // ✅ NEW: lưu ai thao tác (đồng bộ tên cột với hệ thống)
    @Column(name = "nguoi_cap_nhat")
    private Integer nguoiCapNhat;

    // ✅ Optional: join ra nhân viên để lấy tên/mã nếu muốn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_cap_nhat", insertable = false, updatable = false)
    @ToString.Exclude
    private NhanVien nhanVienCapNhat;
}