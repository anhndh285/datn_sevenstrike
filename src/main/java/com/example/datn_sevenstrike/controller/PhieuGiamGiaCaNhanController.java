package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.PhieuGiamGiaCaNhanRequest;
import com.example.datn_sevenstrike.dto.response.PhieuGiamGiaCaNhanResponse;
import com.example.datn_sevenstrike.service.PhieuGiamGiaCaNhanService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/phieu-giam-gia-ca-nhan")
@RequiredArgsConstructor
@Validated
public class PhieuGiamGiaCaNhanController {

    private final PhieuGiamGiaCaNhanService service;

    @GetMapping
    public List<PhieuGiamGiaCaNhanResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public PhieuGiamGiaCaNhanResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public PhieuGiamGiaCaNhanResponse create(@Valid @RequestBody PhieuGiamGiaCaNhanRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public PhieuGiamGiaCaNhanResponse update(@PathVariable("id") Integer id, @Valid @RequestBody PhieuGiamGiaCaNhanRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
