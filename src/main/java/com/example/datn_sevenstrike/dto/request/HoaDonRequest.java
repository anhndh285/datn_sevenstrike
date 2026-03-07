// File: src/main/java/com/example/datn_sevenstrike/dto/request/HoaDonRequest.java
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

    /**
     * ✅ DB mới: tinyint
     * 0: Tại quầy | 1: Giao hàng | 2: Online
     */
    private Integer loaiDon;

    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;

    private String tenKhachHang;
    private String diaChiKhachHang;
    private String soDienThoaiKhachHang;
    private String emailKhachHang;

    /**
     * 1 Chưa xác nhận | 2 Đã xác nhận | 3 Chờ giao | 4 Đang giao | 5 Đã hoàn thành | 6 Đã hủy
     */
    private Integer trangThaiHienTai;

    private LocalDateTime ngayThanhToan;
    private String ghiChu;

    private Boolean xoaMem;

    private Integer nguoiTao;
    private Integer nguoiCapNhat;
    private Integer idGiaoCa;
}