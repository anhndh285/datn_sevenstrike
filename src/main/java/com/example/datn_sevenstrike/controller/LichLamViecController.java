package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.GanNhanVienVaoLichRequest;
import com.example.datn_sevenstrike.dto.request.LichLamViecRequest;
import com.example.datn_sevenstrike.dto.response.LichLamViecResponse;
import com.example.datn_sevenstrike.dto.response.NhanVienTrongCaResponse;
import com.example.datn_sevenstrike.service.LichLamViecService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/lich-lam-viec")
@RequiredArgsConstructor
@Validated
public class LichLamViecController {

    private final LichLamViecService service;

    @GetMapping("/range")
    public List<LichLamViecResponse> range(
            @RequestParam("tuNgay") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam("denNgay") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay
    ) {
        return service.range(tuNgay, denNgay);
    }

    @GetMapping("/{id}")
    public LichLamViecResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public LichLamViecResponse create(@Valid @RequestBody LichLamViecRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public LichLamViecResponse update(@PathVariable Integer id, @Valid @RequestBody LichLamViecRequest req) {
        return service.update(id, req);
    }

    @PostMapping("/{id}/gan-nhan-vien")
    public LichLamViecResponse ganNhanVien(@PathVariable Integer id, @Valid @RequestBody GanNhanVienVaoLichRequest req) {
        return service.ganNhanVien(id, req);
    }

    @GetMapping("/{id}/nhan-vien")
    public List<NhanVienTrongCaResponse> nhanVienTrongCa(@PathVariable Integer id) {
        return service.nhanVienTrongCa(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
