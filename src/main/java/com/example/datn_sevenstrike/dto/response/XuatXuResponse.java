package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XuatXuResponse {
    private Integer id;
    private String maXuatXu;
    private String tenXuatXu;
    private Boolean trangThai;
    private Boolean xoaMem;
}
