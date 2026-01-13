package com.example.datn_sevenstrike.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name = "id_khach_hang", nullable = true)
    private Integer idKhachHang;

    @Column(name = "id_phieu_giam_gia", nullable = true)
    private Integer idPhieuGiamGia;

    @Column(name = "ma_phieu_giam_gia_ca_nhan", nullable = false, insertable = false, updatable = false)
    private String maPhieuGiamGiaCaNhan;

    @Column(name = "ngay_nhan", nullable = true)
    private LocalDate ngayNhan;

    @Column(name = "da_su_dung", nullable = true)
    private Boolean daSuDung;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang", insertable = false, updatable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia", insertable = false, updatable = false)
    private PhieuGiamGia phieuGiamGia;

}
