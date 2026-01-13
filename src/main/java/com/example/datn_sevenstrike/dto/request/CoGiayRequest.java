package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoGiayRequest {
    private String tenCoGiay;
    private Boolean xoaMem;
}