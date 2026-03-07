package com.example.datn_sevenstrike.ghn.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GhnDistrictResponse {
    private Integer districtId;
    private String districtName;
    private Integer provinceId;
}