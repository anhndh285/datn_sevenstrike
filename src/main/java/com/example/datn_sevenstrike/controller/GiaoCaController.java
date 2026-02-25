package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.GiaoCaRequest;
import com.example.datn_sevenstrike.dto.response.GiaoCaResponse;
import com.example.datn_sevenstrike.service.GiaoCaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/giao-ca")
@RequiredArgsConstructor
@Validated
public class GiaoCaController {

    private final GiaoCaService service;

//    Phân trang lịch sử giao ca
//    @GetMapping("/page")
//    public Page<GiaoCaResponse> page(
//            @RequestParam(defaultValue = "0") int pageNo,
//            @RequestParam(defaultValue = "10") int pageSize
//    ) {
//        return service.getPage(pageNo, pageSize);
//    }
//
//    @GetMapping("/{id:\\d+}")
//    public GiaoCaResponse one(@PathVariable("id") Integer id) {
//        return service.getOne(id);
//    }

    // API: Check nhân viên có đang trong ca không (Logic riêng của Giao Ca)
    // Return ResponseEntity để control status code (200 hoặc 204 No Content)
    @GetMapping("/check-active/{idNhanVien:\\d+}")
    public ResponseEntity<GiaoCaResponse> checkActive(@PathVariable("idNhanVien") Integer idNhanVien) {
        try {
            return ResponseEntity.ok(service.getCaLamViecHienTai(idNhanVien));
        } catch (Exception e) {
            // Trả về 204 No Content để FE biết cần hiển thị popup "Bắt đầu ca"
            return ResponseEntity.noContent().build();
        }
    }

    @PutMapping("/xac-nhan-tien/{id:\\d+}")
    public GiaoCaResponse xacNhanTienDauCa(
            @PathVariable("id") Integer id,
            @RequestBody GiaoCaRequest req
    ) {
        // Gọi service và truyền vào ID ca cùng số tiền nhân viên nhập từ FE
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