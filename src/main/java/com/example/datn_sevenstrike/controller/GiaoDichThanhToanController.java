package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.GiaoDichThanhToanRequest;
import com.example.datn_sevenstrike.dto.response.GiaoDichThanhToanResponse;
import com.example.datn_sevenstrike.service.GiaoDichThanhToanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/giao-dich-thanh-toan")
@RequiredArgsConstructor
public class GiaoDichThanhToanController {

    private final GiaoDichThanhToanService service;

    @GetMapping
    public List<GiaoDichThanhToanResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public GiaoDichThanhToanResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public GiaoDichThanhToanResponse create(@RequestBody GiaoDichThanhToanRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public GiaoDichThanhToanResponse update(@PathVariable Integer id, @RequestBody GiaoDichThanhToanRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}

