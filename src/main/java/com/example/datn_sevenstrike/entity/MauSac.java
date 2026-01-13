package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mau_sac")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MauSac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_mau_sac", nullable = false, insertable = false, updatable = false)
    private String maMauSac;

    @Column(name = "ten_mau_sac", nullable = true)
    private String tenMauSac;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

}
