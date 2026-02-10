package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhongCachChoiResponse {
    private Integer id;
    private String maPhongCach;
    private String tenPhongCach;
    private Boolean trangThai;
    private Boolean xoaMem;
}
