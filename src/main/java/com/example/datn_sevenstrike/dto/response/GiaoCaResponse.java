package com.example.datn_sevenstrike.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GiaoCaResponse {
    private Integer id;
    private String maGiaoCa;

    private Integer idLichLamViec;
    private Integer idNhanVien;

    private Integer idGiaoCaTruoc;

    private LocalDateTime thoiGianNhanCa;
    private LocalDateTime thoiGianKetCa;

    private BigDecimal tienBanGiaoDuKien;
    private BigDecimal tienDauCaNhap;
    private Boolean daXacNhanTienDauCa;

    private Integer trangThai;
    private String ghiChu;
}

