package com.example.datn_sevenstrike.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DotGiamGiaRequest {

    private String tenDotGiamGia;

    private Boolean loaiGiamGia;              // DB chốt: chỉ % => luôn false (0)
    private BigDecimal giaTriGiamGia;

    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;

    private Integer mucUuTien;

    private Boolean trangThai;
    private Boolean xoaMem;
}
