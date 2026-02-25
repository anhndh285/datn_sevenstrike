package com.example.datn_sevenstrike.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LichLamViecRequest {
    private Integer idCaLam;
    private LocalDate ngayLam;
    private String ghiChu;

    private Boolean xoaMem;

    private Integer nguoiTao;
}