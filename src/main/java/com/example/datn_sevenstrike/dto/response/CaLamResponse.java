package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaLamResponse {
    private Integer id;
    private String maCa;
    private String tenCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private String moTa;
    private Boolean trangThai;
}