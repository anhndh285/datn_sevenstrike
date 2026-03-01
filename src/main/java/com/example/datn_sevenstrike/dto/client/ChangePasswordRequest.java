package com.example.datn_sevenstrike.dto.client;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
    private String matKhauCu;
    private String matKhauMoi;
}