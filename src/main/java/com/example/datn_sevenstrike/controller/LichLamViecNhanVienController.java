package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.LichLamViecNhanVienRequest;
import com.example.datn_sevenstrike.dto.response.LichLamViecNhanVienResponse;
import com.example.datn_sevenstrike.service.LichLamViecNhanVienService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/lich-lam-viec-nhan-vien")
@RequiredArgsConstructor
@Validated
public class LichLamViecNhanVienController {

    private final LichLamViecNhanVienService service;

    @GetMapping("/nhan-vien/{idNhanVien}")
    public List<LichLamViecNhanVienResponse> one(@PathVariable Integer idNhanVien, @RequestParam LocalDate ngayLam) {
        return service.getByNhanVien(idNhanVien, ngayLam);
    }

    @GetMapping
    public List<LichLamViecNhanVienResponse> getAll() {
        return service.all();
    }

    @PostMapping
    public LichLamViecNhanVienResponse create(@RequestBody LichLamViecNhanVienRequest req) {
        return service.create(req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @PostMapping("/import-excel")
    public List<LichLamViecNhanVienResponse> importExcel(@RequestParam("file") MultipartFile file) {
        return service.importExcel(file);
    }
}
