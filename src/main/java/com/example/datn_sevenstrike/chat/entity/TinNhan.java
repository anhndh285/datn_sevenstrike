package com.example.datn_sevenstrike.chat.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tin_nhan")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class TinNhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phien_chat", nullable = false)
    private PhienChat phienChat;

    // BOT | KHACH | NHAN_VIEN
    @Column(name = "nguoi_gui", length = 20)
    private String nguoiGui;

    @Column(name = "ten_nguoi_gui", length = 255)
    private String tenNguoiGui;

    @Column(name = "noi_dung", columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;
}