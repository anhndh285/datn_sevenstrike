package com.example.datn_sevenstrike.statistics.response;

import lombok.Data;

@Data
public class ProductInventoryStatusResponse {

    private String productCode;
    private String productDetailCode;
    private String productName;

    private String color;
    private String size;
    private String surface;

    private Double price;

    private Integer importQuarter;
    private Integer soldQuarter;

    private Double sellRate;

    private String status;

}
