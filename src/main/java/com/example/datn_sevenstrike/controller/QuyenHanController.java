package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.QuyenHanRequest;
import com.example.datn_sevenstrike.dto.response.QuyenHanResponse;
import com.example.datn_sevenstrike.service.QuyenHanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/quyen-han")
@RequiredArgsConstructor
public class QuyenHanController {

    private final QuyenHanService service;

    @GetMapping
    public List<QuyenHanResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public QuyenHanResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public QuyenHanResponse create(@RequestBody QuyenHanRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public QuyenHanResponse update(@PathVariable Integer id, @RequestBody QuyenHanRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
