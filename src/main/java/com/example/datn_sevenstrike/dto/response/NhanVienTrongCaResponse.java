package com.example.datn_sevenstrike.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NhanVienTrongCaResponse {
    private Integer idNhanVien;
    private String tenTaiKhoan;
}

