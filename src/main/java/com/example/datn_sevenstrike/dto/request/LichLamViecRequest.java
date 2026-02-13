package com.example.datn_sevenstrike.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LichLamViecRequest {

    @NotNull(message = "Id ca làm không được để trống.")
    private Integer idCaLam;

    @NotNull(message = "Ngày làm không được để trống.")
    private LocalDate ngayLam;

    private String ghiChu;
}

