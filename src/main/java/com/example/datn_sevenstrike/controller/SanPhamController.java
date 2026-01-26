package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.SanPhamRequest;
import com.example.datn_sevenstrike.dto.response.SanPhamResponse;
import com.example.datn_sevenstrike.service.SanPhamService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/san-pham")
@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService service;

    @GetMapping
    public List<SanPhamResponse> all() {
        return service.all();
    }

    @GetMapping("/kinh-doanh")
    public List<SanPhamResponse> allKinhDoanh() {
        return service.allKinhDoanh();
    }

    @GetMapping("/{id}")
    public SanPhamResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public SanPhamResponse create(@RequestBody SanPhamRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public SanPhamResponse update(@PathVariable Integer id, @RequestBody SanPhamRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
