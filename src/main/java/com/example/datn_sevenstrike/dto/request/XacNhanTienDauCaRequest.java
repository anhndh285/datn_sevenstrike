package com.example.datn_sevenstrike.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XacNhanTienDauCaRequest {

    @NotNull(message = "Id giao ca không được để trống.")
    private Integer idGiaoCa;

    @NotNull(message = "Tiền đầu ca nhập không được để trống.")
    private BigDecimal tienDauCaNhap;

    // để BE check đúng người (optional)
    private Integer idNhanVien;
}

