package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.HoaDonRequest;
import com.example.datn_sevenstrike.dto.response.HoaDonResponse;
import com.example.datn_sevenstrike.service.HoaDonService;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/hoa-don")
@RequiredArgsConstructor
public class HoaDonController {

    private final HoaDonService service;

    @GetMapping
    public List<HoaDonResponse> all() {
        return service.all();
    }

    @GetMapping("/page")
    public Page<HoaDonResponse> page(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return service.page(pageNo, pageSize);
    }

    @GetMapping("/{id}")
    public HoaDonResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public HoaDonResponse create(@RequestBody HoaDonRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}/confirm-tai-quay-tien-mat")
    public HoaDonResponse confirmTaiQuayTienMat(@PathVariable Integer id, @RequestBody(required = false) NoteBody body) {
        String note = body == null ? null : body.getGhiChu();
        return service.confirmTaiQuayTienMat(id, note);
    }

    @PutMapping("/{id}/trang-thai")
    public HoaDonResponse changeStatus(@PathVariable Integer id, @RequestBody ChangeStatusBody body) {
        return service.changeStatus(id, body.getTrangThai(), body.getGhiChu());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    @Data
    public static class ChangeStatusBody {
        private Integer trangThai; // chỉ nhận: 1,2,3,4,5
        private String ghiChu;
    }

    @Data
    public static class NoteBody {
        private String ghiChu;
    }
}
