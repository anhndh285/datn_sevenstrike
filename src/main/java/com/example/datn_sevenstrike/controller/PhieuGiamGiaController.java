package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.PhieuGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.PhieuGiamGiaResponse;
import com.example.datn_sevenstrike.service.PhieuGiamGiaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/phieu-giam-gia")
@RequiredArgsConstructor
@Validated
public class PhieuGiamGiaController {

    private final PhieuGiamGiaService service;

    @GetMapping
    public List<PhieuGiamGiaResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public PhieuGiamGiaResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public PhieuGiamGiaResponse create(@Valid @RequestBody PhieuGiamGiaRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public PhieuGiamGiaResponse update(@PathVariable("id") Integer id, @Valid @RequestBody PhieuGiamGiaRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
