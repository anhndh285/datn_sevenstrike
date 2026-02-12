package com.example.datn_sevenstrike.statistics.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiniCardResponse {
    private String label;
    private Double revenue;
    private Long totalProducts;
    private Long totalOrders;
}

