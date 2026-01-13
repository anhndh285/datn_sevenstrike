package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.SanPhamRequest;
import com.example.datn_sevenstrike.dto.response.SanPhamResponse;
import com.example.datn_sevenstrike.service.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/san-pham")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService service;

    @GetMapping
    public List<SanPhamResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public SanPhamResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public SanPhamResponse create(@RequestBody SanPhamRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public SanPhamResponse update(@PathVariable Integer id, @RequestBody SanPhamRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
