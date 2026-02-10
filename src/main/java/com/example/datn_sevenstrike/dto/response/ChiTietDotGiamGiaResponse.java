package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietDotGiamGiaResponse {

    private Integer id;

    private Integer idDotGiamGia;
    private Integer idChiTietSanPham;

    private Integer soLuongApDung;
    private BigDecimal giaTriGiamRieng;
    private BigDecimal soTienGiamToiDaRieng;

    private Boolean trangThai;
    private String ghiChu;
    private Boolean xoaMem;

    private LocalDateTime ngayTao;
    private Integer nguoiTao;
    private LocalDateTime ngayCapNhat;
    private Integer nguoiCapNhat;

    // tiện demo
    private String maDotGiamGia;
    private String tenDotGiamGia;

    private String maChiTietSanPham;
    private String maSanPham;
    private String tenSanPham;
}
