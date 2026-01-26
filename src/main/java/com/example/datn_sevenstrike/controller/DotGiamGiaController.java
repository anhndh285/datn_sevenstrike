package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.DotGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.DotGiamGiaResponse;
import com.example.datn_sevenstrike.service.DotGiamGiaService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dot-giam-gia")
@RequiredArgsConstructor
@Validated
public class DotGiamGiaController {

    private final DotGiamGiaService service;

    @GetMapping
    public List<DotGiamGiaResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public DotGiamGiaResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public DotGiamGiaResponse create(@Valid @RequestBody DotGiamGiaRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public DotGiamGiaResponse update(@PathVariable("id") Integer id, @Valid @RequestBody DotGiamGiaRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
