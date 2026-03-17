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

    @Column(name = "id_hoa_don", nullable = false)
    private Integer idHoaDon;

    @Column(name = "id_phuong_thuc_thanh_toan", nullable = false)
    private Integer idPhuongThucThanhToan;

    @Column(name = "ma_giao_dich_thanh_toan", insertable = false, updatable = false, length = 9)
    private String maGiaoDichThanhToan;

    @Column(name = "so_tien", nullable = false, precision = 18, scale = 2)
    private BigDecimal soTien;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai;

    @Column(name = "ma_yeu_cau", length = 100)
    private String maYeuCau;

    @Column(name = "ma_giao_dich_ngoai", length = 100)
    private String maGiaoDichNgoai;

    @Column(name = "ma_tham_chieu", length = 100)
    private String maThamChieu;

    @Lob
    @Column(name = "duong_dan_thanh_toan", columnDefinition = "nvarchar(max)")
    private String duongDanThanhToan;

    @Lob
    @Column(name = "du_lieu_qr", columnDefinition = "nvarchar(max)")
    private String duLieuQr;

    @Column(name = "thoi_gian_het_han")
    private LocalDateTime thoiGianHetHan;

    @Lob
    @Column(name = "du_lieu_phan_hoi", columnDefinition = "nvarchar(max)")
    private String duLieuPhanHoi;

    @Column(name = "thoi_gian_tao", insertable = false, updatable = false)
    private LocalDateTime thoiGianTao;

    @Column(name = "thoi_gian_cap_nhat")
    private LocalDateTime thoiGianCapNhat;

    @Column(name = "ghi_chu", length = 255)
    private String ghiChu;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;

    @Column(name = "nguoi_cap_nhat")
    private Integer nguoiCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don", insertable = false, updatable = false)
    @ToString.Exclude
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phuong_thuc_thanh_toan", insertable = false, updatable = false)
    @ToString.Exclude
    private PhuongThucThanhToan phuongThucThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_cap_nhat", insertable = false, updatable = false)
    @ToString.Exclude
    private NhanVien nhanVienCapNhat;
}