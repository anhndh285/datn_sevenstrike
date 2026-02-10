package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoaiSanResponse {
    private Integer id;
    private String maLoaiSan;
    private String tenLoaiSan;
    private Boolean trangThai;
    private Boolean xoaMem;
}
