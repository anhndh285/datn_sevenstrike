package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.response.KhachHangTongQuanResponse;
import com.example.datn_sevenstrike.repository.HoaDonThongKeRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KhachHangThongKeService {

    private final HoaDonThongKeRepository hoaDonThongKeRepo;

    @Transactional(readOnly = true)
    public KhachHangTongQuanResponse tongQuan(Integer khachHangId) {
        var rowOpt = hoaDonThongKeRepo.findTongQuanByKhachHangId(khachHangId);

        if (rowOpt.isEmpty()) {
            return KhachHangTongQuanResponse.builder()
                    .tongChiTieu(BigDecimal.ZERO)
                    .soDonHang(0L)
                    .donHangGanNhat(null)
                    .build();
        }

        var row = rowOpt.get();
        return KhachHangTongQuanResponse.builder()
                .tongChiTieu(row.getTongChiTieu() != null ? row.getTongChiTieu() : BigDecimal.ZERO)
                .soDonHang(row.getSoDonHang() != null ? row.getSoDonHang() : 0L)
                .donHangGanNhat(row.getDonHangGanNhat())
                .build();
    }
}

