package com.example.datn_sevenstrike.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuGiamGiaRequest {

    private String tenPhieuGiamGia;

    private Boolean loaiPhieuGiamGia;

    private BigDecimal giaTriGiamGia;
    private BigDecimal soTienGiamToiDa;

    private BigDecimal hoaDonToiThieu;
    private Integer soLuongSuDung;

    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;

    private Boolean trangThai;
    private String moTa;

    private Boolean xoaMem;

    private List<Integer> idKhachHangs;
}
