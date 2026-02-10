package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.MauSacRequest;
import com.example.datn_sevenstrike.dto.response.MauSacResponse;
import com.example.datn_sevenstrike.service.MauSacService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/mau-sac")
@RequiredArgsConstructor
public class MauSacController {

    private final MauSacService service;

    @GetMapping
    public List<MauSacResponse> all() {
        return service.all();
    }

    @GetMapping("/active")
    public List<MauSacResponse> allActive() {
        return service.allActive();
    }

    @GetMapping("/{id}")
    public MauSacResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public MauSacResponse create(@RequestBody MauSacRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public MauSacResponse update(@PathVariable Integer id, @RequestBody MauSacRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
