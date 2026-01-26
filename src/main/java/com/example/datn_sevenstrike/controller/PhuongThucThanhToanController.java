package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.PhuongThucThanhToanRequest;
import com.example.datn_sevenstrike.dto.response.PhuongThucThanhToanResponse;
import com.example.datn_sevenstrike.service.PhuongThucThanhToanService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/phuong-thuc-thanh-toan")
@RequiredArgsConstructor
@Validated
public class PhuongThucThanhToanController {

    private final PhuongThucThanhToanService service;

    @GetMapping
    public List<PhuongThucThanhToanResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public PhuongThucThanhToanResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public PhuongThucThanhToanResponse create(@Valid @RequestBody PhuongThucThanhToanRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public PhuongThucThanhToanResponse update(@PathVariable("id") Integer id, @Valid @RequestBody PhuongThucThanhToanRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
