package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.ThuongHieuRequest;
import com.example.datn_sevenstrike.dto.response.ThuongHieuResponse;
import com.example.datn_sevenstrike.service.ThuongHieuService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/thuong-hieu")
@RequiredArgsConstructor
public class ThuongHieuController {

    private final ThuongHieuService service;

    @GetMapping
    public List<ThuongHieuResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public ThuongHieuResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public ThuongHieuResponse create(@RequestBody ThuongHieuRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ThuongHieuResponse update(@PathVariable Integer id, @RequestBody ThuongHieuRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
