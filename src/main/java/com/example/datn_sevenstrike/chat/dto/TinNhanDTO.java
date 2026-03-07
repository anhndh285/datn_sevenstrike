package com.example.datn_sevenstrike.chat.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TinNhanDTO {
    private Integer id;
    private Integer phienChatId;
    private String nguoiGui;
    private String tenNguoiGui;
    private String noiDung;
    private LocalDateTime thoiGian;
}