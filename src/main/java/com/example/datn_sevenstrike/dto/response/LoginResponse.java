package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class LoginResponse {
    private Integer id;
    private String hoTen;
    private String role;
    private String message;
}