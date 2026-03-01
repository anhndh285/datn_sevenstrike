package com.example.datn_sevenstrike.dto.client;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ClientLoginResponse {
    private Integer id;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String anhDaiDien;
    private String role;
    private String message;
}