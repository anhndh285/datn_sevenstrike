package com.example.datn_sevenstrike.dto.request;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHangRequest {

    private String tenKhachHang;
    private String tenTaiKhoan;
    private String matKhau;

    private String email;
    private String soDienThoai;

    private Boolean gioiTinh;
    private LocalDate ngaySinh;

    private Boolean trangThai;
    private Boolean xoaMem;

    private Integer nguoiTao;
    private Integer nguoiCapNhat;
}
