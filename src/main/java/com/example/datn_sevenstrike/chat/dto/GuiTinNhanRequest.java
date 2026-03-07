package com.example.datn_sevenstrike.chat.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class GuiTinNhanRequest {
    private String noiDung;
    private String tenNguoiGui;
    private Integer nhanVienId; // chỉ dùng khi nhân viên gửi
}