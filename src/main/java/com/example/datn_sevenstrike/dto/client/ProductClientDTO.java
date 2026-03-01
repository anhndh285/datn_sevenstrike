package com.example.datn_sevenstrike.dto.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ProductClientDTO {
    private Integer id;
    private String tenSanPham;
    private String tenThuongHieu;
    private String tenXuatXu;
    private String tenViTriThiDau;
    private String tenPhongCachChoi;
    private String tenCoGiay;
    private String tenChatLieu;
    private BigDecimal giaThapNhat;
    private BigDecimal giaCaoNhat;
    private String anhDaiDien;
    private String moTaNgan;

    // discount info
    private BigDecimal giaGocThapNhat;
    private BigDecimal giaSauGiamThapNhat;
    private Integer phanTramGiam;
    private LocalDate ngayKetThucGiamGia;

    // stock & sizes
    private String maSanPham;
    private boolean hangCoSan;
    private List<String> kichThuocCoSan;

    // variants for quick add-to-cart
    private List<VariantClientDTO> variants;
}