package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "thong_bao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_thong_bao", insertable = false, updatable = false, length = 7)
    private String maThongBao;

    @Column(name = "id_nhan_vien_nhan", nullable = false)
    private Integer idNhanVienNhan;

    @Column(name = "loai_thong_bao", nullable = false, length = 50)
    private String loaiThongBao;

    @Column(name = "muc_do", nullable = false)
    private Integer mucDo;

    @Column(name = "tieu_de", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "noi_dung", nullable = false, length = 1000)
    private String noiDung;

    @Column(name = "loai_doi_tuong_lien_quan", length = 50)
    private String loaiDoiTuongLienQuan;

    @Column(name = "id_doi_tuong_lien_quan")
    private Integer idDoiTuongLienQuan;

    @Column(name = "khoa_chong_trung", length = 150)
    private String khoaChongTrung;

    @Lob
    @Column(name = "du_lieu_bo_sung", columnDefinition = "nvarchar(max)")
    private String duLieuBoSung;

    @Column(name = "da_doc", nullable = false)
    private Boolean daDoc;

    @Column(name = "thoi_gian_doc")
    private LocalDateTime thoiGianDoc;

    @Column(name = "da_xu_ly", nullable = false)
    private Boolean daXuLy;

    @Column(name = "thoi_gian_xu_ly")
    private LocalDateTime thoiGianXuLy;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;

    @Column(name = "thoi_gian_tao", nullable = false, insertable = false, updatable = false)
    private LocalDateTime thoiGianTao;

    @Column(name = "nguoi_tao")
    private Integer nguoiTao;

    @Column(name = "thoi_gian_cap_nhat")
    private LocalDateTime thoiGianCapNhat;

    @Column(name = "nguoi_cap_nhat")
    private Integer nguoiCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien_nhan", insertable = false, updatable = false)
    @ToString.Exclude
    private NhanVien nhanVienNhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_tao", insertable = false, updatable = false)
    @ToString.Exclude
    private NhanVien nguoiTaoEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nguoi_cap_nhat", insertable = false, updatable = false)
    @ToString.Exclude
    private NhanVien nguoiCapNhatEntity;
}
