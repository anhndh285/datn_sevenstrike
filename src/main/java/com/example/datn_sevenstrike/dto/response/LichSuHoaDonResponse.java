package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichSuHoaDonResponse {

    private Integer id;
    private Integer idHoaDon;

    private Integer trangThai;

    private String trangThaiLabel;

    private LocalDateTime thoiGian;
    private String ghiChu;

    private Boolean xoaMem;
}
