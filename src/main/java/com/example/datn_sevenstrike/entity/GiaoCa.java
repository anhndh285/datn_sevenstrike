package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "giao_ca")
public class GiaoCa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 7)
    @Column(name = "ma_giao_ca", insertable = false, updatable = false)
    private String maGiaoCa;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_lich_lam_viec")
    private LichLamViec lichLamViec;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @OneToOne
    @JoinColumn(name = "id_giao_ca_truoc")
    private GiaoCa giaoCaTruoc;

    @NotNull
    @ColumnDefault("sysdatetime()")
    @Column(name = "thoi_gian_nhan_ca", nullable = false)
    private LocalDateTime thoiGianNhanCa;

    @Column(name = "thoi_gian_ket_ca")
    private LocalDateTime thoiGianKetCa;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "tien_ban_giao_du_kien", nullable = false, precision = 18, scale = 2)
    private BigDecimal tienBanGiaoDuKien;

    @Column(name = "tien_dau_ca_nhap", precision = 18, scale = 2)
    private BigDecimal tienDauCaNhap;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "da_xac_nhan_tien_dau_ca", nullable = false)
    private Boolean daXacNhanTienDauCa = false;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "trang_thai", nullable = false)
    private Integer trangThai;

    @Size(max = 255)
    @Nationalized
    @Column(name = "ghi_chu")
    private String ghiChu;

    @NotNull
    @ColumnDefault("0")
    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem = false;

    @CreationTimestamp
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "nguoi_tao")
    private Integer nguoiTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "nguoi_cap_nhat")
    private Integer nguoiCapNhat;

}