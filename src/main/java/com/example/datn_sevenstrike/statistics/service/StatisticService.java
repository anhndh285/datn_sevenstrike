package com.example.datn_sevenstrike.statistics.service;

import com.example.datn_sevenstrike.statistics.repository.StatisticRepository;
import com.example.datn_sevenstrike.statistics.response.DetailStatisticResponse;
import com.example.datn_sevenstrike.statistics.response.MiniCardResponse;
import com.example.datn_sevenstrike.statistics.response.OrderStatusResponse;
import com.example.datn_sevenstrike.statistics.response.RevenueChartResponse;
import com.example.datn_sevenstrike.statistics.response.TopProductResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticService {

    private final StatisticRepository repository;

    public StatisticService(StatisticRepository repository) {
        this.repository = repository;
    }

    public Long totalOrders(LocalDate from, LocalDate to) {
        return repository.countTotalOrders(from, to);
    }

    public Double totalRevenue(LocalDate from, LocalDate to) {
        return repository.totalRevenue(from, to);
    }

    public Double realRevenue(LocalDate from, LocalDate to) {
        return repository.realRevenue(from, to);
    }

    public List<TopProductResponse> topProducts(LocalDate from, LocalDate to) {
        return repository.topProducts(from, to);
    }

    public List<OrderStatusResponse> orderStatus(LocalDate from, LocalDate to) {
        return repository.orderStatus(from, to);
    }

    public List<RevenueChartResponse> revenueChart(LocalDate from, LocalDate to) {
        return repository.revenueByDay(from, to);
    }

    // ========== BẢNG THỐNG KÊ CHI TIẾT ==========
    public List<DetailStatisticResponse> getDetailStatisticTable() {

        List<DetailStatisticResponse> result = new ArrayList<>();

        LocalDate today = LocalDate.now();

        result.add(buildRow("Hôm nay", today, today));

        result.add(buildRow("Tuần này", today.minusDays(6), today));

        result.add(buildRow("Tháng này", today.withDayOfMonth(1), today));

        result.add(buildRow("Năm nay", today.withDayOfYear(1), today));

        return result;
    }

    private DetailStatisticResponse buildRow(String label, LocalDate from, LocalDate to) {

        Double revenue = repository.revenueByRange(from, to);
        Long orders = repository.totalOrdersByRange(from, to);

        double avg = (orders == null || orders == 0) ? 0 : revenue / orders;

        long days = ChronoUnit.DAYS.between(from, to) + 1;

        LocalDate prevFrom = from.minusDays(days);
        LocalDate prevTo = from.minusDays(1);

        Double prevRevenue = repository.revenueByRange(prevFrom, prevTo);

        double growth = 0;

        if (prevRevenue != null && prevRevenue != 0) {
            growth = ((revenue - prevRevenue) / prevRevenue) * 100;
        }

        return DetailStatisticResponse.builder()
                .time(label)
                .revenue(revenue)
                .totalOrders(orders)
                .avgPerOrder(avg)
                .growth(growth)
                .build();
    }

    public List<MiniCardResponse> getMiniCards() {

        List<MiniCardResponse> list = new ArrayList<>();

        LocalDate today = LocalDate.now();

        // Hôm nay
        list.add(buildMiniCard("Hôm nay", today, today));

        // Tuần này
        LocalDate startWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        list.add(buildMiniCard("Tuần này", startWeek, today));

        // Tháng này
        LocalDate startMonth = today.withDayOfMonth(1);
        list.add(buildMiniCard("Tháng này", startMonth, today));

        // Năm này
        LocalDate startYear = today.withDayOfYear(1);
        list.add(buildMiniCard("Năm này", startYear, today));

        return list;
    }

    private MiniCardResponse buildMiniCard(String label, LocalDate from, LocalDate to) {

        Double revenue = repository.revenueByRange(from, to);
        Long orders = repository.totalOrdersByRange(from, to);
        Long products = repository.countTotalProductsByRange(from, to);

        return MiniCardResponse.builder()
                .label(label)
                .revenue(revenue)
                .totalOrders(orders)
                .totalProducts(products)
                .build();
    }

}
