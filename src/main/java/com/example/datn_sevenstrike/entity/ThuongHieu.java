package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "thuong_hieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThuongHieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_thuong_hieu", nullable = false, insertable = false, updatable = false)
    private String maThuongHieu;

    @Column(name = "ten_thuong_hieu", nullable = true)
    private String tenThuongHieu;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

}
