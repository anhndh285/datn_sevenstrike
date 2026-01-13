package com.example.datn_sevenstrike.dto.response;

import java.time.LocalDate;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PhieuGiamGiaCaNhanResponse {
    private Integer id;
    private Integer idKhachHang;
    private Integer idPhieuGiamGia;
    private String maPhieuGiamGiaCaNhan;
    private LocalDate ngayNhan;
    private Boolean daSuDung;
    private Boolean xoaMem;
}