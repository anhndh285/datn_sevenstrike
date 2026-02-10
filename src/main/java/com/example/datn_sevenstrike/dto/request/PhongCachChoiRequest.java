package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhongCachChoiRequest {
    private String tenPhongCach;
    private Boolean trangThai;
    private Boolean xoaMem;
}
