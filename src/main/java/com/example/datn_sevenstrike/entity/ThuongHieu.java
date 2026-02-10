package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "thuong_hieu")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ThuongHieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_thuong_hieu", insertable = false, updatable = false, length = 7)
    private String maThuongHieu;

    @Column(name = "ten_thuong_hieu", nullable = false, length = 255)
    private String tenThuongHieu;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
