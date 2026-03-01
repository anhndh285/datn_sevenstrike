package com.example.datn_sevenstrike.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductDetailClientDTO extends ProductClientDTO {
    private String maSanPham;
    private String moTaChiTiet;
    private String tenXuatXu;
    private String tenViTriThiDau;
    private String tenPhongCachChoi;
    private String tenCoGiay;
    private String tenChatLieu;
    private List<String> images;
    private List<VariantClientDTO> variants;
}