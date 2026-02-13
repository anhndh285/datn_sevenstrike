package com.example.datn_sevenstrike.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DongCaRequest {

    @NotNull(message = "Id giao ca không được để trống.")
    private Integer idGiaoCa;

    private String ghiChu;
}
