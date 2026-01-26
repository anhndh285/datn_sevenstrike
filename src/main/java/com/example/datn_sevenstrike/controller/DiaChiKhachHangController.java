package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.DiaChiKhachHangRequest;
import com.example.datn_sevenstrike.dto.response.DiaChiKhachHangResponse;
import com.example.datn_sevenstrike.service.DiaChiKhachHangService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dia-chi-khach-hang")
@RequiredArgsConstructor
@Validated
public class DiaChiKhachHangController {

    private final DiaChiKhachHangService service;

    @GetMapping
    public List<DiaChiKhachHangResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public DiaChiKhachHangResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public DiaChiKhachHangResponse create(@Valid @RequestBody DiaChiKhachHangRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public DiaChiKhachHangResponse update(@PathVariable("id") Integer id, @Valid @RequestBody DiaChiKhachHangRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
