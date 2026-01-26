package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.PhongCachChoiRequest;
import com.example.datn_sevenstrike.dto.response.PhongCachChoiResponse;
import com.example.datn_sevenstrike.service.PhongCachChoiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/phong-cach-choi")
@RequiredArgsConstructor
public class PhongCachChoiController {

    private final PhongCachChoiService service;

    @GetMapping
    public List<PhongCachChoiResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public PhongCachChoiResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public PhongCachChoiResponse create(@RequestBody PhongCachChoiRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public PhongCachChoiResponse update(@PathVariable Integer id, @RequestBody PhongCachChoiRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
