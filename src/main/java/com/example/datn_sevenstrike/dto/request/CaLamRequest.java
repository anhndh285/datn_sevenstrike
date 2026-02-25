package com.example.datn_sevenstrike.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class CaLamRequest {
    @NotNull(message = "Tên ca không được để trống")
    private String tenCa;
    private LocalTime gioBatDau;
    private LocalTime gioKetThuc;
    private String moTa;
    private Boolean trangThai;
}