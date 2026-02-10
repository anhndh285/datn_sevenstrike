package com.example.datn_sevenstrike.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoGiayResponse {
    private Integer id;
    private String maCoGiay;
    private String tenCoGiay;
    private Boolean trangThai;
    private Boolean xoaMem;
}
