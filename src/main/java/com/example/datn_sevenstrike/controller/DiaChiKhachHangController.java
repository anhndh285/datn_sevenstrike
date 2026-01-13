package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.DiaChiKhachHangRequest;
import com.example.datn_sevenstrike.dto.response.DiaChiKhachHangResponse;
import com.example.datn_sevenstrike.service.DiaChiKhachHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dia-chi-khach-hang")
@RequiredArgsConstructor
public class DiaChiKhachHangController {

    private final DiaChiKhachHangService service;

    @GetMapping
    public List<DiaChiKhachHangResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public DiaChiKhachHangResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public DiaChiKhachHangResponse create(@RequestBody DiaChiKhachHangRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public DiaChiKhachHangResponse update(@PathVariable Integer id, @RequestBody DiaChiKhachHangRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
