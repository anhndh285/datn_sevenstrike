package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MauSacRequest {
    private String tenMauSac;
    private Boolean xoaMem;
}
