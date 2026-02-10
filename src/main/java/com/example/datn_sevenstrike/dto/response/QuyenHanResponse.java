package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuyenHanResponse {
    private Integer id;
    private String maQuyenHan;
    private String tenQuyenHan;
    private Boolean trangThai;
    private Boolean xoaMem;
}
