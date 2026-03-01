package com.example.datn_sevenstrike.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherClientDTO {
    private Integer id;
    private String maPhieuGiamGia;
    private String tenPhieuGiamGia;
    private Boolean loaiPhieuGiamGia; // true: percent, false: amount (based on assumption, or vice versa, logic will handle)
    private BigDecimal giaTriGiamGia;
    private BigDecimal soTienGiamToiDa;
    private BigDecimal hoaDonToiThieu;
    private LocalDate ngayKetThuc;
}