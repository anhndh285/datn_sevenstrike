package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DotGiamGiaResponse {

    private Integer id;
    private String maDotGiamGia;

    private String tenDotGiamGia;

    private Boolean loaiGiamGia;
    private BigDecimal giaTriGiamGia;

    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;

    private Integer mucUuTien;
    private Boolean trangThai;
    private Boolean xoaMem;
}
