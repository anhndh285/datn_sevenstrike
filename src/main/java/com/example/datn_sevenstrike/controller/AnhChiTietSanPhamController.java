package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.AnhChiTietSanPhamRequest;
import com.example.datn_sevenstrike.dto.response.AnhChiTietSanPhamResponse;
import com.example.datn_sevenstrike.service.AnhChiTietSanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/anh-chi-tiet-san-pham")
@RequiredArgsConstructor
public class AnhChiTietSanPhamController {

    private final AnhChiTietSanPhamService service;

    @GetMapping
    public List<AnhChiTietSanPhamResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public AnhChiTietSanPhamResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public AnhChiTietSanPhamResponse create(@RequestBody AnhChiTietSanPhamRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public AnhChiTietSanPhamResponse update(@PathVariable Integer id, @RequestBody AnhChiTietSanPhamRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
