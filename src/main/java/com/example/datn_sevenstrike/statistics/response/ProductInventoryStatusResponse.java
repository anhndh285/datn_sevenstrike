package com.example.datn_sevenstrike.statistics.response;

import lombok.Data;

@Data
public class ProductInventoryStatusResponse {

    private String productName;

    private Integer importQuarter;

    private Integer soldQuarter;

    private Double sellRate;

    private String status;
}
