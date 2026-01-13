package com.example.datn_sevenstrike.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chat_lieu")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatLieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_chat_lieu", nullable = false, insertable = false, updatable = false)
    private String maChatLieu;

    @Column(name = "ten_chat_lieu", nullable = true)
    private String tenChatLieu;

    @Column(name = "xoa_mem", nullable = true)
    private Boolean xoaMem;

}
