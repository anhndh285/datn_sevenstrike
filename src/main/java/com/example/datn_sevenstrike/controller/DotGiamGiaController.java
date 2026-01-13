package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.DotGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.DotGiamGiaResponse;
import com.example.datn_sevenstrike.service.DotGiamGiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dot-giam-gia")
@RequiredArgsConstructor
public class DotGiamGiaController {

    private final DotGiamGiaService service;

    @GetMapping
    public List<DotGiamGiaResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public DotGiamGiaResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public DotGiamGiaResponse create(@RequestBody DotGiamGiaRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public DotGiamGiaResponse update(@PathVariable Integer id, @RequestBody DotGiamGiaRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
