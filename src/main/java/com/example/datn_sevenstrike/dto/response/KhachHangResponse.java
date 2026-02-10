package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHangResponse {

    private Integer id;
    private String maKhachHang;

    private String tenKhachHang;
    private String tenTaiKhoan;

    private String email;
    private String soDienThoai;

    private Boolean gioiTinh;
    private LocalDate ngaySinh;

    private Boolean trangThai;
    private Boolean xoaMem;

    private LocalDateTime ngayTao;
    private Integer nguoiTao;
    private LocalDateTime ngayCapNhat;
    private Integer nguoiCapNhat;
}
