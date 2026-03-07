package com.example.datn_sevenstrike.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DongPhienRequest {
    private Integer nguoiDongId; // ID của người thực hiện đóng (nhân viên hoặc admin)
    private String vaiTro;       // "ADMIN" hoặc "NHAN_VIEN"
}