package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KichThuocRequest {
    private String tenKichThuoc;
    private Boolean xoaMem;
}