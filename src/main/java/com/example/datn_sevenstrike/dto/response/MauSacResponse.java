package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MauSacResponse {
    private Integer id;
    private String maMauSac;
    private String tenMauSac;
    private String maMauHex;
    private Boolean trangThai;
    private Boolean xoaMem;
}
