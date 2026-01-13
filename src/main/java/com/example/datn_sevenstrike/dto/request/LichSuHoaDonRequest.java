package com.example.datn_sevenstrike.dto.request;

import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LichSuHoaDonRequest {
    private Integer idHoaDon;
    private String trangThai;
    private LocalDateTime thoiGian;
    private String ghiChu;
    private Boolean xoaMem;
}