package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "giao_dich_thanh_toan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiaoDichThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // DDL: NOT NULL
    @Column(name = "id_hoa_don", nullable = false)
    private Integer idHoaDon;

    // DDL: NOT NULL
    @Column(name = "id_phuong_thuc_thanh_toan", nullable = false)
    private Integer idPhuongThucThanhToan;

    // computed column
    @Column(name = "ma_giao_dich_thanh_toan", insertable = false, updatable = false)
    private String maGiaoDichThanhToan;

    // DDL: NOT NULL, check > 0 (validate ở service)
    @Column(name = "so_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal soTien;

    // DDL: NOT NULL default 'khoi_tao'
    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai;

    // DDL: NULL
    @Column(name = "ma_yeu_cau", length = 100)
    private String maYeuCau;

    // DDL: NULL
    @Column(name = "ma_giao_dich_ngoai", length = 100)
    private String maGiaoDichNgoai;

    // DDL: NULL
    @Column(name = "ma_tham_chieu", length = 100)
    private String maThamChieu;

    // DDL: NULL
    @Column(name = "duong_dan_thanh_toan", length = 500)
    private String duongDanThanhToan;

    // DDL: NULL (nvarchar(max))
    @Lob
    @Column(name = "du_lieu_qr")
    private String duLieuQr;

    // DDL: NULL
    @Column(name = "thoi_gian_het_han")
    private LocalDateTime thoiGianHetHan;

    // DDL: NULL (nvarchar(max))
    @Lob
    @Column(name = "du_lieu_phan_hoi")
    private String duLieuPhanHoi;

    // DDL: NOT NULL default sysdatetime()
    // -> để insertable=false/updatable=false thì DB tự set, khỏi phải gán ở code
    @Column(name = "thoi_gian_tao", nullable = false, insertable = false, updatable = false)
    private LocalDateTime thoiGianTao;

    // DDL: NULL
    @Column(name = "thoi_gian_cap_nhat")
    private LocalDateTime thoiGianCapNhat;

    // DDL: NULL
    @Column(name = "ghi_chu")
    private String ghiChu;

    // DDL: NOT NULL default 0
    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don", insertable = false, updatable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phuong_thuc_thanh_toan", insertable = false, updatable = false)
    private PhuongThucThanhToan phuongThucThanhToan;

    @PrePersist
    public void prePersist() {
        if (xoaMem == null) xoaMem = false;
        if (trangThai == null || trangThai.isBlank()) trangThai = "khoi_tao";
    }

    @PreUpdate
    public void preUpdate() {
        thoiGianCapNhat = LocalDateTime.now();
    }
}
