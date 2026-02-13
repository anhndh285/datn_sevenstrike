package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "lich_lam_viec_nhan_vien")
@Getter
@Setter
public class LichLamViecNhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_lich_lam_viec", nullable = false)
    private Integer idLichLamViec;

    @Column(name = "id_nhan_vien", nullable = false)
    private Integer idNhanVien;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem = false;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "nguoi_tao")
    private Integer nguoiTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "nguoi_cap_nhat")
    private Integer nguoiCapNhat;
}

