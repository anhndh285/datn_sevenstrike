package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "xuat_xu")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class XuatXu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_xuat_xu", insertable = false, updatable = false, length = 7)
    private String maXuatXu;

    @Column(name = "ten_xuat_xu", nullable = false, length = 255)
    private String tenXuatXu;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
