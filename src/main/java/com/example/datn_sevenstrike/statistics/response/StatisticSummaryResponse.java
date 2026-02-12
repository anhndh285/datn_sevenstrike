package com.example.datn_sevenstrike.statistics.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticSummaryResponse {
    private Long totalOrders;
    private Double totalRevenue;
    private Double realRevenue;
}