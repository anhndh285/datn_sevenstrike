package com.example.datn_sevenstrike.dto.response;

import java.time.LocalDateTime;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LichSuHoaDonResponse {
    private Integer id;
    private Integer idHoaDon;
    private String trangThai;
    private LocalDateTime thoiGian;
    private String ghiChu;
    private Boolean xoaMem;
}