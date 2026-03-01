package com.example.datn_sevenstrike.statistics.service;

import com.example.datn_sevenstrike.entity.NhanVien;
import com.example.datn_sevenstrike.repository.NhanVienRepository;
import com.example.datn_sevenstrike.statistics.repository.StatisticRepository;
import com.example.datn_sevenstrike.statistics.response.DetailStatisticResponse;
import com.example.datn_sevenstrike.statistics.response.MiniCardResponse;
import com.example.datn_sevenstrike.statistics.response.OrderStatusResponse;
import com.example.datn_sevenstrike.statistics.response.RevenueChartResponse;
import com.example.datn_sevenstrike.statistics.response.TopProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


import java.time.Month;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final StatisticRepository repository;
    private final NhanVienRepository nhanVienRepository;
    private final EmailService emailService;


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
    public Long getExpectedRevenue(String fromDate, String toDate) {

        LocalDateTime from = LocalDate.parse(fromDate).atStartOfDay();
        LocalDateTime to = LocalDate.parse(toDate).plusDays(1).atStartOfDay();

        BigDecimal result = repository.getExpectedRevenue(from, to);

        return result != null ? result.longValue() : 0L;
    }

// ================= AUTO EXPORT REPORT =================

    public void autoExportRevenue(String type) {

        LocalDate today = LocalDate.now();
        LocalDate from;
        LocalDate to = today;

        String tieuDe;

        switch (type) {

            case "DAY":
                from = today;
                tieuDe = "NGÀY";
                break;

            case "MONTH":
                from = today.withDayOfMonth(1);
                tieuDe = "THÁNG";
                break;

            case "QUARTER":
                int month = today.getMonthValue();
                int startQuarter = ((month - 1) / 3) * 3 + 1;
                from = LocalDate.of(today.getYear(), startQuarter, 1);
                tieuDe = "QUÝ";
                break;

            case "YEAR":
                from = today.withDayOfYear(1);
                tieuDe = "NĂM";
                break;

            default:
                return;
        }

        Double revenue = repository.revenueByRange(from, to);
        Long orders = repository.totalOrdersByRange(from, to);

        if (revenue == null) revenue = 0.0;
        if (orders == null) orders = 0L;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String subject = "===== BÁO CÁO THEO " + tieuDe + " =====";

        String content =
                "<!DOCTYPE html>" +
                        "<html lang='vi'>" +
                        "<head>" +
                        "<meta charset='UTF-8'>" +
                        "</head>" +

                        "<body style='margin:0;padding:0;background:#f4f6f9;font-family:Arial,sans-serif;'>" +

                        "<div style='width:100%;padding:30px 0;background:#f4f6f9;'>" +
                        "<div style='max-width:650px;margin:0 auto;background:#ffffff;border-radius:14px;overflow:hidden;box-shadow:0 10px 30px rgba(0,0,0,0.08);'>" +

                        /* ================= HEADER ================= */

                        "<div style='background:linear-gradient(135deg,#8b0000,#d60000);padding:50px 20px;text-align:center;color:#ffffff;'>" +

                        "<div style='margin:0 auto 30px auto;text-align:center;'>" +
                        "<img src='cid:logo' style='width:130px;height:auto;display:inline-block;" +
                        "filter:drop-shadow(0 10px 25px rgba(0,0,0,0.45));'/>" +
                        "</div>" +

                        "<h1 style='margin:0;font-size:22px;letter-spacing:2px;font-weight:bold;'>BÁO CÁO DOANH THU THEO " + tieuDe + "</h1>" +

                        "</div>" +

                        /* ================= CONTENT ================= */

                        "<div style='padding:35px;color:#333333;font-size:14px;line-height:1.6;'>" +

                        "<p><strong>Kính gửi Quản trị viên,</strong></p>" +
                        "<p>Dưới đây là báo cáo doanh thu hệ thống <strong>SevenStrike</strong>.</p>" +

                        "<div style='background:#fff8f8;border:2px dashed #d60000;border-radius:12px;padding:25px;margin:25px 0;'>" +

                        "<div style='margin-bottom:10px;color:#666;'>Thời gian:</div>" +
                        "<div style='font-weight:bold;margin-bottom:20px;'>" +
                        from.format(formatter) + " - " + to.format(formatter) +
                        "</div>" +

                        "<div style='text-align:center;font-size:26px;color:#d60000;font-weight:bold;margin:20px 0;'>" +
                        String.format("%,.0f", revenue) + " VNĐ" +
                        "</div>" +

                        "<div style='border-top:1px solid #eee;padding-top:15px;margin-top:15px;display:flex;justify-content:space-between;'>" +
                        "<span style='color:#666;'>Tổng số đơn hàng:</span>" +
                        "<span style='font-weight:bold;'>" + orders + "</span>" +
                        "</div>" +

                        "<div style='border-top:1px solid #eee;padding-top:15px;margin-top:15px;display:flex;justify-content:space-between;'>" +
                        "<span style='color:#666;'>Thời điểm gửi:</span>" +
                        "<span style='font-weight:bold;'>" +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +
                        "</span>" +
                        "</div>" +

                        "</div>" +

                        /* ================= BUTTON ================= */

                        "<div style='text-align:center;margin-top:35px;'>" +
                        "<a href='http://localhost:5173/admin/dashboard' " +
                        "style='display:inline-block;" +
                        "padding:16px 45px;" +
                        "background:#d60000;" +
                        "color:#ffffff !important;" +
                        "text-decoration:none !important;" +
                        "border-radius:40px;" +
                        "font-weight:bold;" +
                        "font-size:15px;" +
                        "letter-spacing:1px;" +
                        "box-shadow:0 8px 20px rgba(0,0,0,0.25);'>" +
                        "<span style='color:#ffffff !important;'>XEM CHI TIẾT TRÊN HỆ THỐNG</span>" +
                        "</a>" +
                        "</div>" +

                        "</div>" +

                        /* ================= FOOTER ================= */

                        "<div style='background:linear-gradient(135deg,#1a1a1a,#3b3b3b);padding:25px;text-align:center;color:#ffffff;font-size:12px;'>" +
                        "<p style='margin:0 0 8px 0;font-weight:bold;'>SEVENSTRIKE - NÂNG TẦM PHONG CÁCH</p>" +
                        "<p style='margin:0;'>© " + LocalDate.now().getYear() + " SevenStrike. All rights reserved.</p>" +
                        "<p style='margin:5px 0 0 0;'>Địa chỉ: Hà Nội, Việt Nam</p>" +
                        "</div>" +

                        "</div></div></body></html>";

        List<NhanVien> admins = nhanVienRepository.findAllAdminActive();

        for (NhanVien admin : admins) {
            if (admin.getEmail() != null && !admin.getEmail().isEmpty()) {
                emailService.sendMail(admin.getEmail(), subject, content);
            }
        }

        System.out.println("Đã gửi báo cáo theo " + tieuDe + " cho ADMIN");
    }
}


























