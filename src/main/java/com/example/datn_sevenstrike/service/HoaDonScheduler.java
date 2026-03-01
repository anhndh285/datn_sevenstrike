// File: src/main/java/com/example/datn_sevenstrike/service/HoaDonScheduler.java
package com.example.datn_sevenstrike.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class HoaDonScheduler {

    // ✅ Giờ Hà Nội (UTC+7): dùng ZoneId chuẩn của Việt Nam
    private static final String ZONE_ID = "Asia/Ho_Chi_Minh";
    private static final ZoneId ZONE = ZoneId.of(ZONE_ID);

    private final HoaDonService hoaDonService;

    // 0h00 mỗi ngày theo giờ Hà Nội
    @Scheduled(cron = "0 0 0 * * *", zone = ZONE_ID)
    @Transactional
    public void autoResetHoaDonChoTaiQuayLuc0h() {
        LocalDateTime startToday = LocalDate.now(ZONE).atStartOfDay();
        hoaDonService.resetHoaDonChoTaiQuayCuNgay(startToday);
    }
}