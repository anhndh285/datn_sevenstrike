package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.HoaDonChiTietRequest;
import com.example.datn_sevenstrike.dto.response.HoaDonChiTietResponse;
import com.example.datn_sevenstrike.service.HoaDonChiTietService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/hoa-don-chi-tiet")
@RequiredArgsConstructor
public class HoaDonChiTietController {

    private final HoaDonChiTietService service;

    @GetMapping
    public List<HoaDonChiTietResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public HoaDonChiTietResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public HoaDonChiTietResponse create(@RequestBody HoaDonChiTietRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public HoaDonChiTietResponse update(@PathVariable Integer id, @RequestBody HoaDonChiTietRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
