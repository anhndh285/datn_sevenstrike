package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhuongThucThanhToanResponse {

    private Integer id;

    private String maPhuongThucThanhToan;
    private String tenPhuongThucThanhToan;

    private String nhaCungCap;

    private Boolean trangThai;
    private Boolean xoaMem;
}
