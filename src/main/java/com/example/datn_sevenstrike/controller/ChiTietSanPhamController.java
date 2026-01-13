package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.ChiTietSanPhamRequest;
import com.example.datn_sevenstrike.dto.response.ChiTietSanPhamResponse;
import com.example.datn_sevenstrike.service.ChiTietSanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/chi-tiet-san-pham")
@RequiredArgsConstructor
public class ChiTietSanPhamController {

    private final ChiTietSanPhamService service;

    @GetMapping
    public List<ChiTietSanPhamResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public ChiTietSanPhamResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public ChiTietSanPhamResponse create(@RequestBody ChiTietSanPhamRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public ChiTietSanPhamResponse update(@PathVariable Integer id, @RequestBody ChiTietSanPhamRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
