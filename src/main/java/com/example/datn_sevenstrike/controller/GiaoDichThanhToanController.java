package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.GiaoDichThanhToanRequest;
import com.example.datn_sevenstrike.dto.response.GiaoDichThanhToanResponse;
import com.example.datn_sevenstrike.service.GiaoDichThanhToanService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/giao-dich-thanh-toan")
@RequiredArgsConstructor
@Validated
public class GiaoDichThanhToanController {

    private final GiaoDichThanhToanService service;

    @GetMapping
    public List<GiaoDichThanhToanResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public GiaoDichThanhToanResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public GiaoDichThanhToanResponse create(@Valid @RequestBody GiaoDichThanhToanRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public GiaoDichThanhToanResponse update(@PathVariable("id") Integer id, @Valid @RequestBody GiaoDichThanhToanRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
