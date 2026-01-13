package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "form_chan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FormChan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_form_chan", nullable = false, insertable = false, updatable = false)
    private String maFormChan;

    @Column(name = "ten_form_chan", nullable = true)
    private String tenFormChan;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

}
