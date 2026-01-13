package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.LoaiSanRequest;
import com.example.datn_sevenstrike.dto.response.LoaiSanResponse;
import com.example.datn_sevenstrike.service.LoaiSanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/loai-san")
@RequiredArgsConstructor
public class LoaiSanController {

    private final LoaiSanService service;

    @GetMapping
    public List<LoaiSanResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public LoaiSanResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public LoaiSanResponse create(@RequestBody LoaiSanRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public LoaiSanResponse update(@PathVariable Integer id, @RequestBody LoaiSanRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
