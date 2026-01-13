package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KichThuocResponse {
    private Integer id;
    private String maKichThuoc;
    private String tenKichThuoc;
    private Boolean xoaMem;
}