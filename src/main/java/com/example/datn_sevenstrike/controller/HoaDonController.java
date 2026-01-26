package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.HoaDonRequest;
import com.example.datn_sevenstrike.dto.response.HoaDonResponse;
import com.example.datn_sevenstrike.service.HoaDonService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/hoa-don")
@RequiredArgsConstructor
@Validated
public class HoaDonController {

    private final HoaDonService service;

    @GetMapping
    public List<HoaDonResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public HoaDonResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public HoaDonResponse create(@Valid @RequestBody HoaDonRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public HoaDonResponse update(@PathVariable("id") Integer id, @Valid @RequestBody HoaDonRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
