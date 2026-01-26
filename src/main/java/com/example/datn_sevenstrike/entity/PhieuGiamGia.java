package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
// SỬA IMPORT TẠI ĐÂY: Sử dụng đúng annotation cho Hibernate 6+
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

@Entity
@Table(name = "phieu_giam_gia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // SỬA CẤU HÌNH TẠI ĐÂY:
    // 1. Giữ insertable = false, updatable = false để tránh lỗi chèn vào cột tự toán (Computed Column).
    // 2. Dùng event = EventType.INSERT để Hibernate đọc lại mã từ DB ngay sau khi lưu.
    @Column(name = "ma_phieu_giam_gia", nullable = false, insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private String maPhieuGiamGia;

    @Column(name = "ten_phieu_giam_gia")
    private String tenPhieuGiamGia;

    @Column(name = "loai_phieu_giam_gia")
    private Integer loaiPhieuGiamGia;

    @Column(name = "gia_tri_giam_gia")
    private BigDecimal giaTriGiamGia;

    @Column(name = "so_tien_giam_toi_da", nullable = false)
    private BigDecimal soTienGiamToiDa;

    @Column(name = "hoa_don_toi_thieu", nullable = false)
    private BigDecimal hoaDonToiThieu;

    @Column(name = "so_luong_su_dung")
    private Integer soLuongSuDung;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @Column(name = "mo_ta", nullable = false)
    private String moTa;

    @Column(name = "xoa_mem")
    private Boolean xoaMem;

    @OneToMany(mappedBy = "phieuGiamGia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhieuGiamGiaChiTiet> danhSachKhachHang;
}