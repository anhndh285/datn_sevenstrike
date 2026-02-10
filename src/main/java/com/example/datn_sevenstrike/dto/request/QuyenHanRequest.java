package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuyenHanRequest {
    private String tenQuyenHan;
    private Boolean trangThai;
    private Boolean xoaMem;
}
