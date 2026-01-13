package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.PhuongThucThanhToanRequest;
import com.example.datn_sevenstrike.dto.response.PhuongThucThanhToanResponse;
import com.example.datn_sevenstrike.service.PhuongThucThanhToanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/phuong-thuc-thanh-toan")
@RequiredArgsConstructor
public class PhuongThucThanhToanController {

    private final PhuongThucThanhToanService service;

    @GetMapping
    public List<PhuongThucThanhToanResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public PhuongThucThanhToanResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public PhuongThucThanhToanResponse create(@RequestBody PhuongThucThanhToanRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public PhuongThucThanhToanResponse update(@PathVariable Integer id, @RequestBody PhuongThucThanhToanRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
