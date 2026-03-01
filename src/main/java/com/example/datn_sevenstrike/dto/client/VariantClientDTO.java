package com.example.datn_sevenstrike.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VariantClientDTO {
    private Integer id;
    private String tenMauSac;
    private String maMauHex;
    private String tenKichThuoc;
    private String tenLoaiSan;
    private String tenFormChan;
    private BigDecimal giaBan;
    private Integer soLuong;
    private String anhDaiDien;
    // discount info
    private BigDecimal giaGoc;
    private BigDecimal giaSauGiam;
    private Integer phanTramGiam;
    private LocalDate ngayKetThuc;
}