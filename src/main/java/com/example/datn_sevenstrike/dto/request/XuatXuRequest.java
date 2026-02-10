package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XuatXuRequest {
    private String tenXuatXu;
    private Boolean trangThai;
    private Boolean xoaMem;
}
