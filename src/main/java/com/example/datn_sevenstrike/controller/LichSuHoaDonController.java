package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.LichSuHoaDonRequest;
import com.example.datn_sevenstrike.dto.response.LichSuHoaDonResponse;
import com.example.datn_sevenstrike.service.LichSuHoaDonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lich-su-hoa-don")
@RequiredArgsConstructor
@Validated
public class LichSuHoaDonController {

    private final LichSuHoaDonService service;

    @GetMapping
    public List<LichSuHoaDonResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public LichSuHoaDonResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public LichSuHoaDonResponse create(@Valid @RequestBody LichSuHoaDonRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public LichSuHoaDonResponse update(@PathVariable("id") Integer id, @Valid @RequestBody LichSuHoaDonRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}

