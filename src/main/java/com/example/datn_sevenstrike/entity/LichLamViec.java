package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "lich_lam_viec")
public class LichLamViec {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ca_lam", nullable = false)
    private CaLam idCaLam;

    @NotNull
    @Column(name = "ngay_lam", nullable = false)
    private LocalDate ngayLam;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ghi_chu")
    private String ghiChu;

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