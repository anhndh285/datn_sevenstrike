package com.example.datn_sevenstrike.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonRequest {

    private Integer idKhachHang;
    private Integer idNhanVien;

    private Integer idPhieuGiamGia;
    private Integer idPhieuGiamGiaCaNhan;

    private Boolean loaiDon; // 0: tại quầy | 1: giao hàng/online

    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;

    private String tenKhachHang;
    private String diaChiKhachHang;
    private String soDienThoaiKhachHang;
    private String emailKhachHang;

    private Integer trangThaiHienTai;

    private LocalDateTime ngayThanhToan;
    private String ghiChu;

    private Boolean xoaMem;

    private Integer nguoiTao;
    private Integer nguoiCapNhat;
}
