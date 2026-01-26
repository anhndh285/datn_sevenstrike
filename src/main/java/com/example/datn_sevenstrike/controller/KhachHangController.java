package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.KhachHangRequest;
import com.example.datn_sevenstrike.dto.response.KhachHangResponse;
import com.example.datn_sevenstrike.service.KhachHangService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/khach-hang")
@RequiredArgsConstructor
@Validated
public class KhachHangController {

    private final KhachHangService service;

    @GetMapping
    public List<KhachHangResponse> all() {
        return service.all();
    }

    // ✅ NEW: phân trang (FE đang dùng pageNo/pageSize)
    @GetMapping("/page")
    public Page<KhachHangResponse> page(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return service.page(pageNo, pageSize);
    }

    // ✅ FIX: chỉ match id là số để tránh /page bị hiểu là id
    @GetMapping("/{id:\\d+}")
    public KhachHangResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public KhachHangResponse create(@Valid @RequestBody KhachHangRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id:\\d+}")
    public KhachHangResponse update(@PathVariable("id") Integer id, @Valid @RequestBody KhachHangRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
