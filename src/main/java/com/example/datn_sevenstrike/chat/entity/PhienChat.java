package com.example.datn_sevenstrike.chat.entity;

import com.example.datn_sevenstrike.entity.KhachHang;
import com.example.datn_sevenstrike.entity.NhanVien;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "phien_chat")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PhienChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @Column(name = "ten_khach", length = 255)
    private String tenKhach;

    // KHACH_HANG (khách → nhân viên) | NOI_BO (nhân viên → admin)
    @Column(name = "loai", length = 20)
    private String loai;

    @Column(name = "trang_thai", length = 50)
    private String trangThai;

    @Column(name = "thoi_gian_bat_dau")
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc")
    private LocalDateTime thoiGianKetThuc;
}