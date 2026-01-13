package com.example.datn_sevenstrike.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietDotGiamGiaRequest {
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
}