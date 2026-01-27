package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.NhanVienRequest;
import com.example.datn_sevenstrike.dto.response.NhanVienResponse;
import com.example.datn_sevenstrike.service.NhanVienService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/page")
    public Page<NhanVienResponse> page(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return service.page(pageNo, pageSize);
    }

    @GetMapping("/{id:\\d+}")
    public NhanVienResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    // =========================
    // CREATE
    // =========================

    // JSON (không file)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public NhanVienResponse createJson(@Valid @RequestBody NhanVienRequest req) {
        return service.create(req);
    }

    // multipart (có file)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public NhanVienResponse createForm(
            @RequestPart("data") @Valid NhanVienRequest req,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return service.create(req, file);
    }

    // =========================
    // UPDATE
    // =========================

    // JSON (không file)
    @PutMapping(value = "/{id:\\d+}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public NhanVienResponse updateJson(
            @PathVariable("id") Integer id,
            @Valid @RequestBody NhanVienRequest req
    ) {
        return service.update(id, req);
    }

    // multipart (có file)
    @PutMapping(value = "/{id:\\d+}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public NhanVienResponse updateForm(
            @PathVariable("id") Integer id,
            @RequestPart("data") @Valid NhanVienRequest req,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return service.update(id, req, file);
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
