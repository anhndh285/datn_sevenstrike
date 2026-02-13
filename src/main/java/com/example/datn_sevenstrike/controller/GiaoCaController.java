package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.DongCaRequest;
import com.example.datn_sevenstrike.dto.request.GiaoCaVaoCaRequest;
import com.example.datn_sevenstrike.dto.request.XacNhanTienDauCaRequest;
import com.example.datn_sevenstrike.dto.response.GiaoCaResponse;
import com.example.datn_sevenstrike.service.GiaoCaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/giao-ca")
@RequiredArgsConstructor
@Validated
public class GiaoCaController {

    private final GiaoCaService service;

    @GetMapping("/dang-hoat-dong/{idNhanVien}")
    public GiaoCaResponse dangHoatDong(@PathVariable Integer idNhanVien) {
        return service.dangHoatDong(idNhanVien);
    }

    @PostMapping("/vao-ca")
    public GiaoCaResponse vaoCa(@Valid @RequestBody GiaoCaVaoCaRequest req) {
        return service.vaoCa(req);
    }

    @PostMapping("/xac-nhan-tien-dau-ca")
    public GiaoCaResponse xacNhanTienDauCa(@Valid @RequestBody XacNhanTienDauCaRequest req) {
        return service.xacNhanTienDauCa(req);
    }

    @PostMapping("/dong-ca")
    public GiaoCaResponse dongCa(@Valid @RequestBody DongCaRequest req) {
        return service.dongCa(req);
    }
}
