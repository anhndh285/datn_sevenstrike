package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.LichSuHoaDonRequest;
import com.example.datn_sevenstrike.dto.response.LichSuHoaDonResponse;
import com.example.datn_sevenstrike.service.LichSuHoaDonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lich-su-hoa-don")
@RequiredArgsConstructor
public class LichSuHoaDonController {

    private final LichSuHoaDonService service;

    @GetMapping
    public List<LichSuHoaDonResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public LichSuHoaDonResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public LichSuHoaDonResponse create(@RequestBody LichSuHoaDonRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public LichSuHoaDonResponse update(@PathVariable Integer id, @RequestBody LichSuHoaDonRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
