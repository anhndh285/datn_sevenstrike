package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.HoaDonChiTietRequest;
import com.example.datn_sevenstrike.dto.response.HoaDonChiTietResponse;
import com.example.datn_sevenstrike.service.HoaDonChiTietService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/hoa-don-chi-tiet")
@RequiredArgsConstructor
@Validated
public class HoaDonChiTietController {

    private final HoaDonChiTietService service;

    @GetMapping
    public List<HoaDonChiTietResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public HoaDonChiTietResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public HoaDonChiTietResponse create(@Valid @RequestBody HoaDonChiTietRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public HoaDonChiTietResponse update(@PathVariable("id") Integer id, @Valid @RequestBody HoaDonChiTietRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
