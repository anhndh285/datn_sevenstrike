// File: src/main/java/com/example/datn_sevenstrike/dto/request/HoaDonChiTietRequest.java
package com.example.datn_sevenstrike.dto.request;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoaDonChiTietRequest {

    private Integer idHoaDon;
    private Integer idChiTietSanPham;

    private Integer soLuong;
    private BigDecimal donGia;

    private String ghiChu;
    private Boolean xoaMem;

    // ✅ Các trường để xử lý tạo bản ghi mới khi giá thay đổi
    private Integer soLuongTangThem;    // Số lượng tăng thêm
    private Boolean isGiaDaThayDoi;    // Flag kiểm tra giá có thay đổi không
    private BigDecimal giaBanLuc;      // Giá lúc mở modal (giá cũ)
    private BigDecimal giaBanHienTai;  // Giá hiện tại từ server
    private Integer idHoaDonChiTiet;   // hoa_don_chi_tiet.id — để xác định đúng bản ghi khi cùng ctspId có nhiều dòng
}