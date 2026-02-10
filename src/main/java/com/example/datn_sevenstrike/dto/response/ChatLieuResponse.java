package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLieuResponse {
    private Integer id;
    private String maChatLieu;
    private String tenChatLieu;
    private Boolean trangThai;
    private Boolean xoaMem;
}
