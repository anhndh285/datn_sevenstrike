package com.example.datn_sevenstrike.statistics.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RevenueChartResponse {

    private String time;
    private Double revenue;

    public RevenueChartResponse(Object time, Object revenue) {
        this.time = time != null ? time.toString() : "";
        this.revenue = revenue != null ? Double.valueOf(revenue.toString()) : 0.0;
    }
}
