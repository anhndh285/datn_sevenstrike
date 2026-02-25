package com.example.datn_sevenstrike.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhachHangTongQuanResponse {
    private BigDecimal tongChiTieu;
    private Long soDonHang;
    private LocalDateTime donHangGanNhat;
}

