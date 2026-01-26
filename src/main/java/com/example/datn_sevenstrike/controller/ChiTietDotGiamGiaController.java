package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.ChiTietDotGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.ChiTietDotGiamGiaResponse;
import com.example.datn_sevenstrike.service.ChiTietDotGiamGiaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/chi-tiet-dot-giam-gia")
@RequiredArgsConstructor
@Validated
public class ChiTietDotGiamGiaController {

    private final ChiTietDotGiamGiaService service;

    @GetMapping
    public List<ChiTietDotGiamGiaResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public ChiTietDotGiamGiaResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public ChiTietDotGiamGiaResponse create(@Valid @RequestBody ChiTietDotGiamGiaRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ChiTietDotGiamGiaResponse update(@PathVariable("id") Integer id, @Valid @RequestBody ChiTietDotGiamGiaRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
