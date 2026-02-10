package com.example.datn_sevenstrike.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietSanPhamRequest {

    private Integer idSanPham;

    private Integer idMauSac;
    private Integer idKichThuoc;
    private Integer idLoaiSan;
    private Integer idFormChan;

    private Integer soLuong;
    private BigDecimal giaNiemYet;
    private BigDecimal giaBan;

    private Boolean trangThai;
    private String ghiChu;

    private Boolean xoaMem;

    private Integer nguoiTao;
    private Integer nguoiCapNhat;
}
