package com.example.datn_sevenstrike.statistics.controller;

import com.example.datn_sevenstrike.statistics.response.MiniCardResponse;
import com.example.datn_sevenstrike.statistics.response.OrderStatusResponse;
import com.example.datn_sevenstrike.statistics.response.RevenueChartResponse;
import com.example.datn_sevenstrike.statistics.response.TopProductResponse;
import com.example.datn_sevenstrike.statistics.service.StatisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistic")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class StatisticController {

    private final StatisticService service;

    public StatisticController(StatisticService service) {
        this.service = service;
    }

    @GetMapping("/total-orders")
    public Long totalOrders(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {
        return service.totalOrders(fromDate, toDate);
    }

    @GetMapping("/total-revenue")
    public Double totalRevenue(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {
        return service.totalRevenue(fromDate, toDate);
    }

    @GetMapping("/real-revenue")
    public Double realRevenue(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {
        return service.realRevenue(fromDate, toDate);
    }

    @GetMapping("/top-products")
    public List<TopProductResponse> topProducts(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {
        return service.topProducts(fromDate, toDate);
    }

    @GetMapping("/order-status")
    public List<OrderStatusResponse> orderStatus(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {
        return service.orderStatus(fromDate, toDate);
    }

    @GetMapping("/revenue-chart")
    public List<RevenueChartResponse> revenueChart(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate
    ) {
        return service.revenueChart(fromDate, toDate);
    }

    @GetMapping("/detail-table")
    public ResponseEntity<?> getDetailTable() {
        return ResponseEntity.ok(service.getDetailStatisticTable());
    }
    @GetMapping("/mini-cards")
    public List<MiniCardResponse> getMiniCards() {
        return service.getMiniCards();
    }

}
