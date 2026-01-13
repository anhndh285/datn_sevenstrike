package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.PhieuGiamGiaCaNhanRequest;
import com.example.datn_sevenstrike.dto.response.PhieuGiamGiaCaNhanResponse;
import com.example.datn_sevenstrike.service.PhieuGiamGiaCaNhanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/phieu-giam-gia-ca-nhan")
@RequiredArgsConstructor
public class PhieuGiamGiaCaNhanController {

    private final PhieuGiamGiaCaNhanService service;

    @GetMapping
    public List<PhieuGiamGiaCaNhanResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public PhieuGiamGiaCaNhanResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public PhieuGiamGiaCaNhanResponse create(@RequestBody PhieuGiamGiaCaNhanRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public PhieuGiamGiaCaNhanResponse update(@PathVariable Integer id, @RequestBody PhieuGiamGiaCaNhanRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
