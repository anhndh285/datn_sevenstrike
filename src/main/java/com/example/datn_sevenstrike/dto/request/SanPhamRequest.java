package com.example.datn_sevenstrike.dto.request;

import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
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
    private Boolean xoaMem;
    private LocalDateTime ngayTao;
    private Integer nguoiTao;
    private LocalDateTime ngayCapNhat;
    private Integer nguoiCapNhat;
    private Boolean trangThaiKinhDoanh;
}
