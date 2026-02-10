package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLieuRequest {
    private String tenChatLieu;
    private Boolean trangThai;
    private Boolean xoaMem;
}
