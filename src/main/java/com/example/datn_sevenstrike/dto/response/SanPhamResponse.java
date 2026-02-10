package com.example.datn_sevenstrike.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SanPhamResponse {

    private Integer id;

    private Integer idThuongHieu;
    private Integer idXuatXu;
    private Integer idViTriThiDau;
    private Integer idPhongCachChoi;
    private Integer idCoGiay;
    private Integer idChatLieu;

    private String maSanPham;
    private String tenSanPham;

    private String moTaNgan;
    private String moTaChiTiet;

    private Boolean trangThaiKinhDoanh;
    private Boolean xoaMem;

    private LocalDateTime ngayTao;
    private Integer nguoiTao;
    private LocalDateTime ngayCapNhat;
    private Integer nguoiCapNhat;

    // tiện demo hiển thị tên thuộc tính
    private String maThuongHieu;
    private String tenThuongHieu;

    private String maXuatXu;
    private String tenXuatXu;

    private String maViTri;
    private String tenViTri;

    private String maPhongCach;
    private String tenPhongCach;

    private String maCoGiay;
    private String tenCoGiay;

    private String maChatLieu;
    private String tenChatLieu;
}
