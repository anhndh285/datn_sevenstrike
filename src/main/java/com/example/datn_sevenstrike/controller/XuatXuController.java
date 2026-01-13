package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.XuatXuRequest;
import com.example.datn_sevenstrike.dto.response.XuatXuResponse;
import com.example.datn_sevenstrike.service.XuatXuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/xuat-xu")
@RequiredArgsConstructor
public class XuatXuController {

    private final XuatXuService service;

    @GetMapping
    public List<XuatXuResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public XuatXuResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public XuatXuResponse create(@RequestBody XuatXuRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public XuatXuResponse update(@PathVariable Integer id, @RequestBody XuatXuRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
