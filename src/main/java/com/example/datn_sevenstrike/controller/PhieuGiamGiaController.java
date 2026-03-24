// File: src/main/java/com/example/datn_sevenstrike/controller/PhieuGiamGiaController.java
package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.GuiMailPhieuGiamGiaRequest;
import com.example.datn_sevenstrike.dto.request.PhieuGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.GuiMailPhieuGiamGiaResponse;
import com.example.datn_sevenstrike.dto.response.PhieuGiamGiaResponse;
import com.example.datn_sevenstrike.service.PhieuGiamGiaService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/phieu-giam-gia")
@RequiredArgsConstructor
public class PhieuGiamGiaController {

    private final PhieuGiamGiaService service;

    @GetMapping
    public List<PhieuGiamGiaResponse> all(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayBatDau,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayKetThuc,
            @RequestParam(required = false) Boolean trangThai
    ) {
        return service.all(keyword, ngayBatDau, ngayKetThuc, trangThai);
    }

    @GetMapping("/{id}")
    public PhieuGiamGiaResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public PhieuGiamGiaResponse create(@RequestBody PhieuGiamGiaRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public PhieuGiamGiaResponse update(@PathVariable Integer id, @RequestBody PhieuGiamGiaRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @GetMapping("/{id}/khach-hang-ids")
    public List<Integer> getCustomerIdsByVoucher(@PathVariable Integer id) {
        return service.getCustomerIdsByVoucher(id);
    }

    @GetMapping("/{id}/khach-hang-da-gui-ids")
    public List<Integer> getKhachHangDaGuiIds(@PathVariable Integer id) {
        return service.getKhachHangDaGuiIds(id);
    }

    @PostMapping("/{id}/gui-mail")
    public GuiMailPhieuGiamGiaResponse guiMail(@PathVariable Integer id, @RequestBody GuiMailPhieuGiamGiaRequest req) {
        return service.guiMail(id, req);
    }
}