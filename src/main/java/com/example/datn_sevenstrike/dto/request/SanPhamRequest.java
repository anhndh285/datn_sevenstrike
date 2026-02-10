package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamRequest {

    private Integer idThuongHieu;
    private Integer idXuatXu;

    private Integer idViTriThiDau;
    private Integer idPhongCachChoi;

    private Integer idCoGiay;
    private Integer idChatLieu;

    private String tenSanPham;
    private String moTaNgan;
    private String moTaChiTiet;

    private Boolean trangThaiKinhDoanh;
    private Boolean xoaMem;

    private Integer nguoiTao;
    private Integer nguoiCapNhat;
}
