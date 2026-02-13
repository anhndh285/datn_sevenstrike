package com.example.datn_sevenstrike.dto.response;

import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;

@Getter
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

