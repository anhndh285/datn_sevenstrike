package com.example.datn_sevenstrike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ThongBaoScheduler {

    private final ThongBaoService thongBaoService;

    @Scheduled(
            initialDelayString = "${thongbao.quet.don-cho-xac-nhan.initial-delay-ms:20000}",
            fixedDelayString = "${thongbao.quet.don-cho-xac-nhan.fixed-delay-ms:60000}"
    )
    public void quetDonChoXacNhan() {
        thongBaoService.quetDonChoXacNhan();
    }

    @Scheduled(
            initialDelayString = "${thongbao.quet.thanh-toan-lech-trang-thai.initial-delay-ms:25000}",
            fixedDelayString = "${thongbao.quet.thanh-toan-lech-trang-thai.fixed-delay-ms:60000}"
    )
    public void quetThanhToanThanhCongChuaDongBo() {
        thongBaoService.quetThanhToanThanhCongChuaDongBo();
    }

    @Scheduled(
            initialDelayString = "${thongbao.quet.giao-dich-bat-thuong.initial-delay-ms:30000}",
            fixedDelayString = "${thongbao.quet.giao-dich-bat-thuong.fixed-delay-ms:120000}"
    )
    public void quetGiaoDichBatThuong() {
        thongBaoService.quetGiaoDichBatThuong();
    }

    @Scheduled(
            initialDelayString = "${thongbao.quet.ton-kho.initial-delay-ms:35000}",
            fixedDelayString = "${thongbao.quet.ton-kho.fixed-delay-ms:180000}"
    )
    public void quetTonKho() {
        thongBaoService.quetTonKho();
    }

    @Scheduled(
            initialDelayString = "${thongbao.quet.chat.initial-delay-ms:40000}",
            fixedDelayString = "${thongbao.quet.chat.fixed-delay-ms:60000}"
    )
    public void quetKhachChoPhanHoiQuaHan() {
        thongBaoService.quetKhachChoPhanHoiQuaHan();
    }
}
