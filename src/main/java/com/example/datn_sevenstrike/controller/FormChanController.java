package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.FormChanRequest;
import com.example.datn_sevenstrike.dto.response.FormChanResponse;
import com.example.datn_sevenstrike.service.FormChanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/form-chan")
@RequiredArgsConstructor
public class FormChanController {

    private final FormChanService service;

    @GetMapping
    public List<FormChanResponse> all() {
        return service.all();
    }

    @GetMapping("/<built-in function id>")
    public FormChanResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public FormChanResponse create(@RequestBody FormChanRequest req) {
        return service.create(req);
    }

    @PutMapping("/<built-in function id>")
    public FormChanResponse update(@PathVariable Integer id, @RequestBody FormChanRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/<built-in function id>")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
