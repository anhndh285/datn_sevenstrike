package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.KichThuocRequest;
import com.example.datn_sevenstrike.dto.response.KichThuocResponse;
import com.example.datn_sevenstrike.service.KichThuocService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/kich-thuoc")
@RequiredArgsConstructor
public class KichThuocController {

    private final KichThuocService service;

    @GetMapping
    public List<KichThuocResponse> all() {
        return service.all();
    }

    @GetMapping("/active")
    public List<KichThuocResponse> allActive() {
        return service.allActive();
    }

    @GetMapping("/{id}")
    public KichThuocResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public KichThuocResponse create(@RequestBody KichThuocRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public KichThuocResponse update(@PathVariable Integer id, @RequestBody KichThuocRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
