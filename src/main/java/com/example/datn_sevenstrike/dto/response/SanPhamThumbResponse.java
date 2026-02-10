package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamThumbResponse {

    private Integer idSanPham;
    private String maSanPham;
    private String tenSanPham;
    private Boolean trangThaiKinhDoanh;

    private Integer idCtspThumb;
    private String duongDanAnhThumb;
}
