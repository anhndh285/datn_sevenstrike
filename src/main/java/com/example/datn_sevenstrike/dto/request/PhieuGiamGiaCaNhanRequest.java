package com.example.datn_sevenstrike.dto.request;

import java.time.LocalDate;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhieuGiamGiaCaNhanRequest {
    private Integer idKhachHang;
    private Integer idPhieuGiamGia;
    private LocalDate ngayNhan;
    private Boolean daSuDung;
    private Boolean xoaMem;
}
