package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.ChiTietDotGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.ChiTietDotGiamGiaResponse;
import com.example.datn_sevenstrike.service.ChiTietDotGiamGiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/chi-tiet-dot-giam-gia")
@RequiredArgsConstructor
public class ChiTietDotGiamGiaController {

    private final ChiTietDotGiamGiaService service;

    @GetMapping
    public List<ChiTietDotGiamGiaResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public ChiTietDotGiamGiaResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public ChiTietDotGiamGiaResponse create(@RequestBody ChiTietDotGiamGiaRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public ChiTietDotGiamGiaResponse update(@PathVariable Integer id, @RequestBody ChiTietDotGiamGiaRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
