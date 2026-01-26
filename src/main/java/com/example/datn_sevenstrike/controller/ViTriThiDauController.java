package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.ViTriThiDauRequest;
import com.example.datn_sevenstrike.dto.response.ViTriThiDauResponse;
import com.example.datn_sevenstrike.service.ViTriThiDauService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/vi-tri-thi-dau")
@RequiredArgsConstructor
public class ViTriThiDauController {

    private final ViTriThiDauService service;

    @GetMapping
    public List<ViTriThiDauResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public ViTriThiDauResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public ViTriThiDauResponse create(@RequestBody ViTriThiDauRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ViTriThiDauResponse update(@PathVariable Integer id, @RequestBody ViTriThiDauRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
