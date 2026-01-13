package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "lich_su_hoa_don")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuHoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "id_hoa_don", nullable = false)
    private Integer idHoaDon;

    @Column(name = "trang_thai", nullable = false, length = 50)
    private String trangThai;

    // DB default sysdatetime()
    @Column(name = "thoi_gian", nullable = false, insertable = false, updatable = false)
    private LocalDateTime thoiGian;

    @Column(name = "ghi_chu", nullable = true, length = 255)
    private String ghiChu;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don", insertable = false, updatable = false)
    private HoaDon hoaDon;
}
