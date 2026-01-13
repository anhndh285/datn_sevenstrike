package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "kich_thuoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KichThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_kich_thuoc", nullable = false, insertable = false, updatable = false)
    private String maKichThuoc;

    @Column(name = "ten_kich_thuoc", nullable = true)
    private String tenKichThuoc;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

}
