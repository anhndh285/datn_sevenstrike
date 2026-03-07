package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.GiaoCaRequest;
import com.example.datn_sevenstrike.dto.response.GiaoCaResponse;
import com.example.datn_sevenstrike.service.GiaoCaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/giao-ca")
@RequiredArgsConstructor
@Validated
public class GiaoCaController {

    private final GiaoCaService service;

    // Trả lại hàm page gọn gàng như ban đầu
    @GetMapping("/page")
    public ResponseEntity<Page<GiaoCaResponse>> page(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "100") int pageSize
    ) {
        return ResponseEntity.ok(service.getPage(pageNo, pageSize));
    }

    @GetMapping("/check-active/{idNhanVien:\\d+}")
    public ResponseEntity<GiaoCaResponse> checkActive(@PathVariable("idNhanVien") Integer idNhanVien) {
        try {
            return ResponseEntity.ok(service.getCaLamViecHienTai(idNhanVien));
        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/xac-nhan-tien/{id:\\d+}")
    public GiaoCaResponse xacNhanTienDauCa(
            @PathVariable("id") Integer id,
            @RequestBody GiaoCaRequest req
    ) {
        return service.xacNhanTienDauCa(id, req.getTienDauCaNhap());
    }

    @PostMapping("/bat-dau")
    public GiaoCaResponse batDau(@Valid @RequestBody GiaoCaRequest req) {
        return service.batDauCa(req);
    }

    @PutMapping("/ket-thuc/{id:\\d+}")
    public GiaoCaResponse ketThuc(@PathVariable("id") Integer id, @Valid @RequestBody GiaoCaRequest req) {
        return service.ketThucCa(id, req);
    }
}