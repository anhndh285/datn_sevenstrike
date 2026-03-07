package com.example.datn_sevenstrike.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PhienChatDTO {
    private Integer id;
    private String tenKhach;
    private String loai;
    private String trangThai;
    private LocalDateTime thoiGianBatDau;
    private String tinNhanCuoi;
    private int soTinNhan;
    private Integer nhanVienId;
}