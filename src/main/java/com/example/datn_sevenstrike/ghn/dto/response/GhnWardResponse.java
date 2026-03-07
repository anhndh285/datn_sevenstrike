package com.example.datn_sevenstrike.ghn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GhnWardResponse {
    private String wardCode;
    private String wardName;
    private Integer districtId;
}