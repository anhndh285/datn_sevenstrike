package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KichThuocResponse {
    private Integer id;
    private String maKichThuoc;
    private String tenKichThuoc;
    private BigDecimal giaTriKichThuoc;
    private Boolean trangThai;
    private Boolean xoaMem;
}
