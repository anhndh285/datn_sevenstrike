package com.example.datn_sevenstrike.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

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
    private LocalDateTime ngayTao;
    private Integer nguoiTao;
    private LocalDateTime ngayCapNhat;
    private Integer nguoiCapNhat;
}