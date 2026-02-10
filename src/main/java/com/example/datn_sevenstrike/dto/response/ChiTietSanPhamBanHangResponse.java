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
    private BigDecimal giaBan;
    private String anhUrl;
}
