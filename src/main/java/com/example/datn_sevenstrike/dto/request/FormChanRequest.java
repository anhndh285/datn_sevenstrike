package com.example.datn_sevenstrike.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FormChanRequest {
    private String tenFormChan;
    private Boolean xoaMem;
}