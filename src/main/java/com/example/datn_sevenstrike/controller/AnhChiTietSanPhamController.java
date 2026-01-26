package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.AnhChiTietSanPhamRequest;
import com.example.datn_sevenstrike.dto.response.AnhChiTietSanPhamResponse;
import com.example.datn_sevenstrike.service.AnhChiTietSanPhamService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/anh-chi-tiet-san-pham")
@RequiredArgsConstructor
public class AnhChiTietSanPhamController {

    private final AnhChiTietSanPhamService service;

    @GetMapping
    public List<AnhChiTietSanPhamResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public AnhChiTietSanPhamResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public AnhChiTietSanPhamResponse create(@RequestBody AnhChiTietSanPhamRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public AnhChiTietSanPhamResponse update(@PathVariable Integer id, @RequestBody AnhChiTietSanPhamRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping("/by-chi-tiet-san-pham/{idChiTietSanPham}")
    public List<AnhChiTietSanPhamResponse> byChiTietSanPham(@PathVariable Integer idChiTietSanPham) {
        return service.byChiTietSanPham(idChiTietSanPham);
    }

    @PutMapping("/{id}/set-dai-dien")
    public AnhChiTietSanPhamResponse setDaiDien(@PathVariable Integer id) {
        return service.setDaiDien(id);
    }

    // ✅ Upload ảnh mới cho CTSP (tạo record mới)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnhChiTietSanPhamResponse upload(
            @RequestParam Integer idChiTietSanPham,
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "false") boolean laAnhDaiDien,
            @RequestParam(required = false) String moTa
    ) {
        return service.upload(idChiTietSanPham, file, laAnhDaiDien, moTa);
    }

    // ✅ Update/Replace file ảnh theo ID ảnh (phục vụ “update ảnh bị lỗi”)
    @PutMapping(value = "/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnhChiTietSanPhamResponse uploadUpdate(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Boolean laAnhDaiDien,
            @RequestParam(required = false) String moTa
    ) {
        return service.uploadUpdate(id, file, laAnhDaiDien, moTa);
    }
}
