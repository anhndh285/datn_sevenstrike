package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.LichSuHoaDonRequest;
import com.example.datn_sevenstrike.dto.response.LichSuHoaDonResponse;
import com.example.datn_sevenstrike.service.LichSuHoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lich-su-hoa-don")
@RequiredArgsConstructor
public class LichSuHoaDonController {

    private final LichSuHoaDonService service;

    @GetMapping
    public List<LichSuHoaDonResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public LichSuHoaDonResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    // ✅ timeline theo hóa đơn (asc theo thời gian)
    @GetMapping("/by-hoa-don/{idHoaDon}")
    public List<LichSuHoaDonResponse> byHoaDon(@PathVariable Integer idHoaDon) {
        return service.byHoaDon(idHoaDon);
    }

    @PostMapping
    public LichSuHoaDonResponse create(@RequestBody LichSuHoaDonRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public LichSuHoaDonResponse update(@PathVariable Integer id, @RequestBody LichSuHoaDonRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
