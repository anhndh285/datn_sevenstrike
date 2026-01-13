package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "co_giay")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoGiay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_co_giay", nullable = false, insertable = false, updatable = false)
    private String maCoGiay;

    @Column(name = "ten_co_giay", nullable = true)
    private String tenCoGiay;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

}