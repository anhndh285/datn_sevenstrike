package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_lieu")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ChatLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_chat_lieu", insertable = false, updatable = false, length = 7)
    private String maChatLieu;

    @Column(name = "ten_chat_lieu", nullable = false, length = 255)
    private String tenChatLieu;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai;

    @Column(name = "xoa_mem", nullable = false)
    private Boolean xoaMem;
}
