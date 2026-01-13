package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.HoaDonRequest;
import com.example.datn_sevenstrike.dto.response.HoaDonResponse;
import com.example.datn_sevenstrike.service.HoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/hoa-don")
@RequiredArgsConstructor
public class HoaDonController {

    private final HoaDonService service;

    @GetMapping
    public List<HoaDonResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public HoaDonResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public HoaDonResponse create(@RequestBody HoaDonRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public HoaDonResponse update(@PathVariable Integer id, @RequestBody HoaDonRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
