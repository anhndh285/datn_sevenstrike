package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.KhachHangRequest;
import com.example.datn_sevenstrike.dto.response.KhachHangResponse;
import com.example.datn_sevenstrike.service.KhachHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/khach-hang")
@RequiredArgsConstructor
public class KhachHangController {

    private final KhachHangService service;

    @GetMapping
    public List<KhachHangResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public KhachHangResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public KhachHangResponse create(@RequestBody KhachHangRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public KhachHangResponse update(@PathVariable Integer id, @RequestBody KhachHangRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
