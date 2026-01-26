package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.NhanVienRequest;
import com.example.datn_sevenstrike.dto.response.NhanVienResponse;
import com.example.datn_sevenstrike.service.NhanVienService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/nhan-vien")
@RequiredArgsConstructor
@Validated
public class NhanVienController {

    private final NhanVienService service;

    @GetMapping
    public List<NhanVienResponse> all() {
        return service.all();
    }

    // ✅ NEW: phân trang (FE đang dùng pageNo/pageSize)
    @GetMapping("/page")
    public Page<NhanVienResponse> page(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return service.page(pageNo, pageSize);
    }

    // ✅ FIX: chỉ nhận id là số
    @GetMapping("/{id:\\d+}")
    public NhanVienResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public NhanVienResponse create(@Valid @RequestBody NhanVienRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id:\\d+}")
    public NhanVienResponse update(@PathVariable("id") Integer id, @Valid @RequestBody NhanVienRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
