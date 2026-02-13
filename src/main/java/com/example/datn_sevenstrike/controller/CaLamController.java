package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.CaLamRequest;
import com.example.datn_sevenstrike.dto.response.CaLamResponse;
import com.example.datn_sevenstrike.service.CaLamService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public CaLamResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public CaLamResponse create(@Valid @RequestBody CaLamRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public CaLamResponse update(@PathVariable Integer id, @Valid @RequestBody CaLamRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
