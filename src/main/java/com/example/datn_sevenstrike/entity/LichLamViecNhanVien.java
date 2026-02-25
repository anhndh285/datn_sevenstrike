package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "lich_lam_viec_nhan_vien")
public class LichLamViecNhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lich_lam_viec", nullable = false)
    private LichLamViec lichLamViec;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_nhan_vien", nullable = false)
    private NhanVien nhanVien;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem = false;

    @NotNull
    @ColumnDefault("sysdatetime()")
    @Column(name = "ngay_tao", nullable = false)
    private Instant ngayTao;

    @Column(name = "nguoi_tao")
    private Integer nguoiTao;

    @Column(name = "ngay_cap_nhat")
    private Instant ngayCapNhat;

    @Column(name = "nguoi_cap_nhat")
    private Integer nguoiCapNhat;

}