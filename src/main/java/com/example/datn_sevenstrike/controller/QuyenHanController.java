package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.QuyenHanRequest;
import com.example.datn_sevenstrike.dto.response.QuyenHanResponse;
import com.example.datn_sevenstrike.service.QuyenHanService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/quyen-han")
@RequiredArgsConstructor
@Validated
public class QuyenHanController {

    private final QuyenHanService service;

    @GetMapping
    public List<QuyenHanResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public QuyenHanResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public QuyenHanResponse create(@Valid @RequestBody QuyenHanRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public QuyenHanResponse update(@PathVariable("id") Integer id, @Valid @RequestBody QuyenHanRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
