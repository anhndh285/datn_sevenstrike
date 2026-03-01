package com.example.datn_sevenstrike.dto.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRegisterRequest {
    private String tenKhachHang;
    private String email;
    private String soDienThoai;
    private String matKhau;
}