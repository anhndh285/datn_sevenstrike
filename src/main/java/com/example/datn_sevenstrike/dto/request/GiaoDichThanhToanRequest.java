package com.example.datn_sevenstrike.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiaoDichThanhToanRequest {

    private Integer idHoaDon;
    private Integer idPhuongThucThanhToan;

    private BigDecimal soTien;
    private String trangThai;

    private String maYeuCau;
    private String maGiaoDichNgoai;
    private String maThamChieu;

    private String duongDanThanhToan;
    private String duLieuQr;
    private LocalDateTime thoiGianHetHan;

    private String duLieuPhanHoi;
    private LocalDateTime thoiGianCapNhat;

    private String ghiChu;
    private Boolean xoaMem;
}
