package com.example.datn_sevenstrike.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CaLamRequest {

    @NotBlank(message = "Tên ca không được để trống.")
    private String tenCa;

    @NotNull(message = "Giờ bắt đầu không được để trống.")
    private LocalTime gioBatDau;

    @NotNull(message = "Giờ kết thúc không được để trống.")
    private LocalTime gioKetThuc;

    private String moTa;

    private Boolean trangThai;
}
