package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThuongHieuRequest {
    private String tenThuongHieu;
    private Boolean trangThai;
    private Boolean xoaMem;
}
