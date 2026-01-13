package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.NhanVienRequest;
import com.example.datn_sevenstrike.dto.response.NhanVienResponse;
import com.example.datn_sevenstrike.service.NhanVienService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/nhan-vien")
@RequiredArgsConstructor
public class NhanVienController {

    private final NhanVienService service;

    @GetMapping
    public List<NhanVienResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public NhanVienResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public NhanVienResponse create(@RequestBody NhanVienRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public NhanVienResponse update(@PathVariable Integer id, @RequestBody NhanVienRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
