package com.example.datn_sevenstrike.dto.response;

import com.example.datn_sevenstrike.entity.LichLamViec;
import com.example.datn_sevenstrike.entity.NhanVien;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LichLamViecNhanVienResponse {
    private Integer id;

    // Thông tin lịch
    private LichLamViec lichLamViec;
    private String tenCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private LocalDate ngayLam;

    // Thông tin nhân viên
    private NhanVien nhanVien;
    private String maNhanVien;
    private String tenNhanVien;
    private String tenTaiKhoan;

    private Boolean xoaMem;
}