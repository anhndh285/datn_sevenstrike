package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.CaLamRequest;
import com.example.datn_sevenstrike.dto.response.CaLamResponse;
import com.example.datn_sevenstrike.service.CaLamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/ca-lam")
@RequiredArgsConstructor
@Validated
public class CaLamController {
    private final CaLamService service;

    @GetMapping
    public List<CaLamResponse> all() {
        return service.all();
    }

    // ✅ FIX: chỉ match id là số để tránh /page bị hiểu là id
    @GetMapping("/{id:\\d+}")
    public CaLamResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public CaLamResponse create(@Valid @RequestBody CaLamRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id:\\d+}")
    public CaLamResponse update(@PathVariable("id") Integer id, @Valid @RequestBody CaLamRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
