package com.example.datn_sevenstrike.dto.request;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GiaoCaVaoCaRequest {

    @NotNull(message = "Id lịch làm việc không được để trống.")
    private Integer idLichLamViec;

    @NotNull(message = "Id nhân viên không được để trống.")
    private Integer idNhanVien;

    // tiền bàn giao dự kiến (nếu không truyền sẽ mặc định 0)
    private BigDecimal tienBanGiaoDuKien;
}

