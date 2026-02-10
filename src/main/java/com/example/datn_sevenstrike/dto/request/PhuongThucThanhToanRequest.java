package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhuongThucThanhToanRequest {

    private String tenPhuongThucThanhToan;
    private String nhaCungCap;

    private Boolean trangThai;
    private Boolean xoaMem;
}
