package com.example.datn_sevenstrike.statistics.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.example.datn_sevenstrike.statistics.service.StatisticService;

@Component
@RequiredArgsConstructor
public class RevenueScheduler {

    private final StatisticService statisticService;

    // Cuối ngày
    @Scheduled(cron = "0 59 23 * * ?", zone = "Asia/Ho_Chi_Minh")
    public void dailyReport() {
        statisticService.autoExportRevenue("DAY");
    }

    // Cuối tháng
    @Scheduled(cron = "0 59 23 L * ?", zone = "Asia/Ho_Chi_Minh")
    public void monthlyReport() {
        statisticService.autoExportRevenue("MONTH");
    }

    // Cuối quý
    @Scheduled(cron = "0 59 23 L 3,6,9,12 ?", zone = "Asia/Ho_Chi_Minh")
    public void quarterlyReport() {
        statisticService.autoExportRevenue("QUARTER");
    }

    // Cuối năm
    @Scheduled(cron = "0 59 23 31 12 ?", zone = "Asia/Ho_Chi_Minh")
    public void yearlyReport() {
        statisticService.autoExportRevenue("YEAR");
    }
}