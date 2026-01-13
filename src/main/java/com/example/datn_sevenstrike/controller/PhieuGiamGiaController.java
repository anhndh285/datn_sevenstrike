package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.PhieuGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.PhieuGiamGiaResponse;
import com.example.datn_sevenstrike.service.PhieuGiamGiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/phieu-giam-gia")
@RequiredArgsConstructor
public class PhieuGiamGiaController {

    private final PhieuGiamGiaService service;

    @GetMapping
    public List<PhieuGiamGiaResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public PhieuGiamGiaResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public PhieuGiamGiaResponse create(@RequestBody PhieuGiamGiaRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public PhieuGiamGiaResponse update(@PathVariable Integer id, @RequestBody PhieuGiamGiaRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
