package com.example.datn_sevenstrike.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NhanVienRequest {
    private Integer idQuyenHan;
    private String tenNhanVien;
    private String tenTaiKhoan;
    private String matKhau;
    private String email;
    private String soDienThoai;
    private String anhNhanVien;
    private LocalDate ngaySinh;
    private String ghiChu;
    private String thanhPho;
    private String quan;
    private String phuong;
    private String diaChiCuThe;
    private String cccd;
    private Boolean trangThai;
    private Boolean xoaMem;
    private LocalDateTime ngayTao;
    private Integer nguoiTao;
    private LocalDateTime ngayCapNhat;
    private Integer nguoiCapNhat;
}
