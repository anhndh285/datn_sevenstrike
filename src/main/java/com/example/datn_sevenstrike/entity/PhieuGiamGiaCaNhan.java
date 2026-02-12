package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "phieu_giam_gia_ca_nhan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGiaCaNhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_khach_hang", nullable = false)
    private Integer idKhachHang;

    @Column(name = "id_phieu_giam_gia", nullable = false)
    private Integer idPhieuGiamGia;

    @Column(name = "ma_phieu_giam_gia_ca_nhan", insertable = false, updatable = false, length = 10)
    private String maPhieuGiamGiaCaNhan;

    @Column(name = "ngay_nhan", nullable = false)
    private LocalDate ngayNhan;

    @Column(name = "da_su_dung", nullable = false)
    private Boolean daSuDung;

    // ✅ NEW
    @Column(name = "da_gui_mail", nullable = false)
    private Boolean daGuiMail;

    // ✅ NEW
    @Column(name = "ngay_gui_mail")
    private LocalDateTime ngayGuiMail;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang", insertable = false, updatable = false)
    @ToString.Exclude
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia", insertable = false, updatable = false)
    @ToString.Exclude
    private PhieuGiamGia phieuGiamGia;
}
