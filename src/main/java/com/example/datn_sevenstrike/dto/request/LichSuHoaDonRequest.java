package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuHoaDonRequest {

    private Integer idHoaDon;

    private Integer trangThai;

    private String ghiChu;
    private Boolean xoaMem;
}
