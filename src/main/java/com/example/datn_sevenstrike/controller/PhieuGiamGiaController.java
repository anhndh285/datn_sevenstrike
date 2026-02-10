package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.PhieuGiamGiaRequest;
import com.example.datn_sevenstrike.dto.response.PhieuGiamGiaResponse;
import com.example.datn_sevenstrike.service.PhieuGiamGiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/phieu-giam-gia")
@RequiredArgsConstructor
public class PhieuGiamGiaController {

    private final PhieuGiamGiaService service;

    @GetMapping
    public List<PhieuGiamGiaResponse> all() {
        return service.all();
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

    // ✅ lấy danh sách id khách hàng của voucher (để FE prefill khi sửa)
    @GetMapping("/{id}/khach-hang-ids")
    public List<Integer> getCustomerIdsByVoucher(@PathVariable Integer id) {
        return service.getCustomerIdsByVoucher(id);
    }
}
