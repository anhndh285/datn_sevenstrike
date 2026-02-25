package com.example.datn_sevenstrike.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GiaoCaRequest {
    private Integer idLichLamViec;
    // Input khi Bắt đầu ca
    private Integer idNhanVien;
    private BigDecimal tienDauCaNhap;

    private String ghiChu;
}