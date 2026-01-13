package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.CoGiayRequest;
import com.example.datn_sevenstrike.dto.response.CoGiayResponse;
import com.example.datn_sevenstrike.service.CoGiayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/co-giay")
@RequiredArgsConstructor
public class CoGiayController {

    private final CoGiayService service;

    @GetMapping
    public List<CoGiayResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public CoGiayResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public CoGiayResponse create(@RequestBody CoGiayRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public CoGiayResponse update(@PathVariable Integer id, @RequestBody CoGiayRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
