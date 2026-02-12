// File: src/main/java/com/example/datn_sevenstrike/controller/ChiTietSanPhamBanHangController.java
package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.response.ChiTietSanPhamBanHangResponse;
import com.example.datn_sevenstrike.service.ChiTietSanPhamService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chi-tiet-san-pham")
@RequiredArgsConstructor
public class ChiTietSanPhamBanHangController {

    private final ChiTietSanPhamService service;

    // FE gọi: /api/chi-tiet-san-pham/ban-hang hoặc /api/admin/chi-tiet-san-pham/ban-hang
    @GetMapping("/ban-hang")
    public List<ChiTietSanPhamBanHangResponse> banHang() {
        return service.banHang();
    }

    // FE fallback: /api/chi-tiet-san-pham hoặc /api/admin/chi-tiet-san-pham
    @GetMapping
    public List<ChiTietSanPhamBanHangResponse> allForPos() {
        return service.banHang();
    }
}
