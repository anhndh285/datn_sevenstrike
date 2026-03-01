package com.example.datn_sevenstrike.dto.client;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientOrderHistoryDTO {
    private Integer id;
    private String maHoaDon;
    private LocalDateTime ngayTao;
    private String trangThai; // Label
    private BigDecimal tongTien;
    private String sanPhamDaiDien; // Name of first product or summary
    private String anhDaiDien; // Thumb of first product
    private int soLuongSanPham;
}