package com.example.datn_sevenstrike.statistics.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopProductResponse {
    private String productDetailCode;
    private String productName;
    private BigDecimal price;
    private Long quantity;
    private String color;
    private String size;
    private String surface;
    private String imageUrl;
    private Integer stockQuantity; // Số lượng tồn kho thực tế
}