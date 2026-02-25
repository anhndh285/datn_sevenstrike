package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.LichLamViecRequest;
import com.example.datn_sevenstrike.dto.response.LichLamViecResponse;
import com.example.datn_sevenstrike.service.LichLamViecService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lich-lam-viec")
@RequiredArgsConstructor
@Validated
public class LichLamViecController {

    private final LichLamViecService service;

    @GetMapping("/page")
    public Page<LichLamViecResponse> page(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return service.getpage(pageNo, pageSize);
    }

    @GetMapping("/check")
    public List<LichLamViecResponse> checkLich(@RequestParam("ca") Integer idCa,
                                               @RequestParam("ngay") String ngay) {
        return service.checkCa(idCa, ngay);
    }

    @GetMapping
    public List<LichLamViecResponse> all() {
        return service.all();
    }

    @PostMapping
    public LichLamViecResponse create(@Valid @RequestBody LichLamViecRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id:\\d+}")
    public LichLamViecResponse update(@PathVariable("id") Integer id, @Valid @RequestBody LichLamViecRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }

    @PostMapping("/import-excel")
    public List<LichLamViecResponse> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            // Service sẽ trả về danh sách các bản ghi vừa thêm
            return service.importExcel(file);
        } catch (Exception e) {
            // Ném lỗi Runtime để Spring Boot tự trả về lỗi 500 hoặc 400
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage());
        }
    }
}