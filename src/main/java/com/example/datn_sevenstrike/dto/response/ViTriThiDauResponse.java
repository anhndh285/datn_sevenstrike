package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViTriThiDauResponse {
    private Integer id;
    private String maViTri;
    private String tenViTri;
    private Boolean trangThai;
    private Boolean xoaMem;
}
