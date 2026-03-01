// File: src/main/java/com/example/datn_sevenstrike/dto/request/XacNhanThanhToanTaiQuayRequest.java
package com.example.datn_sevenstrike.dto.request;

import java.math.BigDecimal;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class XacNhanThanhToanTaiQuayRequest {

    private String ghiChu;

    /**
     * Danh sách thanh toán (split payment)
     * - tenPhuongThuc: "Tiền mặt" | "Chuyển khoản" (đúng theo DB)
     * - soTien: > 0
     * - Tổng soTien phải = tong_tien_sau_giam
     */
    private List<ThanhToanTaiQuayItem> thanhToans;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ThanhToanTaiQuayItem {
        private String tenPhuongThuc;   // "Tiền mặt", "Chuyển khoản"
        private BigDecimal soTien;      // > 0
        private String maThamChieu;     // optional (thường dùng cho chuyển khoản)
        private String ghiChu;          // optional
    }
}