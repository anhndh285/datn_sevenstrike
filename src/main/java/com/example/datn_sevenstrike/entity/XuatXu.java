package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "xuat_xu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XuatXu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_xuat_xu", nullable = false, insertable = false, updatable = false)
    private String maXuatXu;

    @Column(name = "ten_xuat_xu", nullable = true)
    private String tenXuatXu;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

}

