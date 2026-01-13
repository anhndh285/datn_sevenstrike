package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HoaDonResponse {
    private Integer id;
    private Integer idKhachHang;
    private Integer idNhanVien;
    private Integer idPhieuGiamGia;
    private Integer idPhieuGiamGiaCaNhan;
    private String maHoaDon;
    private Boolean loaiDon;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;
    private BigDecimal tongTienGiam;
    private String tenKhachHang;
    private String diaChiKhachHang;
    private String soDienThoaiKhachHang;
    private String emailKhachHang;
    private String trangThaiHienTai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayThanhToan;
    private String ghiChu;
    private Boolean xoaMem;
    private Integer nguoiTao;
    private LocalDateTime ngayCapNhat;
    private Integer nguoiCapNhat;
}