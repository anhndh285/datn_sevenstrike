// File: src/main/java/com/example/datn_sevenstrike/dto/response/ChiTietSanPhamBanHangResponse.java
package com.example.datn_sevenstrike.dto.response;

import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiTietSanPhamBanHangResponse {

    private Integer id;
    private String maCtsp;
    private String tenSanPham;
    private String mauSac;
    private String kichCo;
    private Integer soLuong;

    // Giá gốc (ctsp.gia_ban)
    private BigDecimal giaGoc;

    // Giá bán thực tế sau khi áp dụng đợt giảm giá (nếu có)
    private BigDecimal giaBan;

    // % giảm đang áp dụng (0 nếu không có)
    private BigDecimal phanTramGiam;

    // Thông tin đợt giảm giá đang áp dụng (null nếu không có)
    private Integer idDotGiamGia;
    private String maDotGiamGia;
    private String tenDotGiamGia;

    private String anhUrl;
}
