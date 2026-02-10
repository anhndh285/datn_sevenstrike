package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiaoDichThanhToanResponse {

    private Integer id;

    private Integer idHoaDon;
    private Integer idPhuongThucThanhToan;

    private String maGiaoDichThanhToan;

    private BigDecimal soTien;
    private String trangThai;

    private String maYeuCau;
    private String maGiaoDichNgoai;
    private String maThamChieu;

    private String duongDanThanhToan;
    private String duLieuQr;
    private LocalDateTime thoiGianHetHan;

    private String duLieuPhanHoi;

    private LocalDateTime thoiGianTao;
    private LocalDateTime thoiGianCapNhat;

    private String ghiChu;
    private Boolean xoaMem;

    // tiện demo
    private String maHoaDon;
    private String maPhuongThucThanhToan;
    private String tenPhuongThucThanhToan;
}
