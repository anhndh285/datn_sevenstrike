package com.example.datn_sevenstrike.ghn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GhnTinhPhiResponse {
    private long phiVanChuyen;
    private Integer serviceId;
    private long serviceFee;
    private long insuranceFee;
    private long total;
}