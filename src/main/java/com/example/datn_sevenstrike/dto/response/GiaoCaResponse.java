package com.example.datn_sevenstrike.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiaoCaResponse {
    private Integer id;
    private String maGiaoCa;
    private BigDecimal tienBanGiaoDuKien;
    private BigDecimal tienDauCaNhap;
    private Boolean daXacNhanTienDauCa;
    private LocalDateTime thoiGianNhanCa;
    private LocalDateTime thoiGianKetCa;
    private Integer trangThai;
    private String ghiChu;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private Integer nguoiTao;
    private Integer nguoiCapNhat;

    // ==========================================
    // ✅ CÁC BIẾN BỔ SUNG ĐỂ HIỂN THỊ LÊN GIAO DIỆN
    // ==========================================
    private String tenNhanVien;
    private String tenCaLam;
    private LocalTime gioBatDauCa;
    private LocalTime gioKetThucCa;

    // Các biến chứa tiền
    private BigDecimal tongTienTrongCa;
    private BigDecimal tienMatTrongCa;
    private BigDecimal tienChuyenKhoanTrongCa;
}