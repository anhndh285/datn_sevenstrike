package com.example.datn_sevenstrike.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KichThuocRequest {
    private String tenKichThuoc;           // hiển thị
    private BigDecimal giaTriKichThuoc;    // 38.0 -> 45.0 (nullable)
    private Boolean trangThai;
    private Boolean xoaMem;
}
