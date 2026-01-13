package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ViTriThiDauRequest {
    private String tenViTri;
    private Boolean xoaMem;
}