package com.example.datn_sevenstrike.chat.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class KhoiTaoPhienRequest {
    private String tenKhach;
    private Integer khachHangId;  // null nếu khách vãng lai (KHACH_HANG)
    private String loai;          // KHACH_HANG | NOI_BO (mặc định KHACH_HANG)
    private Integer nhanVienId;   // dùng khi loai = NOI_BO
}