package com.example.datn_sevenstrike.dto.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Integer idKhachHang;
    private String tenKhachHang;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String ghiChu;
    private Integer idPhieuGiamGia;
    private Integer loaiThanhToan;          // 0: COD, 1: VNPay/Chuyển khoản
    private Integer idPhuongThucThanhToan;  // ID trực tiếp nếu FE đã biết
    private List<OrderItemRequest> items;
    private Integer ghnToDistrictId;        // GHN district ID để tính phí vận chuyển
    private String ghnToWardCode;           // GHN ward code để tính phí vận chuyển
}