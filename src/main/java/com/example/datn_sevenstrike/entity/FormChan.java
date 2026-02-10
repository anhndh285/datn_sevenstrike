package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "form_chan")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class FormChan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_form_chan", insertable = false, updatable = false, length = 7)
    private String maFormChan;

    @Column(name = "ten_form_chan", nullable = false, length = 255)
    private String tenFormChan;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
