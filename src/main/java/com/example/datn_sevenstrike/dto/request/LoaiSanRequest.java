package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoaiSanRequest {
    private String tenLoaiSan;
    private Boolean trangThai;
    private Boolean xoaMem;
}
