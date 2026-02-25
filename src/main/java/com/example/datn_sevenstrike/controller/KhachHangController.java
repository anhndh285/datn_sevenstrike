package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.KhachHangRequest;
import com.example.datn_sevenstrike.dto.response.KhachHangResponse;
import com.example.datn_sevenstrike.dto.response.KhachHangTongQuanResponse;
import com.example.datn_sevenstrike.service.KhachHangService;
import com.example.datn_sevenstrike.service.KhachHangThongKeService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/khach-hang")
@RequiredArgsConstructor
@Validated
public class KhachHangController {

    private final KhachHangService service;
    private final KhachHangThongKeService thongKeService;

    // ✅ Alias cho FE đang gọi /all
    @GetMapping("/all")
    public List<KhachHangResponse> allAlias() {
        return service.all();
    }

    // ✅ Endpoint chuẩn (giữ lại)
    @GetMapping
    public List<KhachHangResponse> all() {
        return service.all();
    }

    @GetMapping("/page")
    public Page<KhachHangResponse> page(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int pageSize
    ) {
        return service.page(pageNo, pageSize);
    }

    // ✅ chỉ match số để tránh nuốt nhầm 'page', 'all'
    @GetMapping("/{id:\\d+}")
    public KhachHangResponse one(@PathVariable("id") Integer id) {
        return service.one(id);
    }

    @PostMapping
    public KhachHangResponse create(@Valid @RequestBody KhachHangRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id:\\d+}")
    public KhachHangResponse update(@PathVariable("id") Integer id, @Valid @RequestBody KhachHangRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }

    // ========================= THỐNG KÊ KHÁCH HÀNG (3 CỘT) =========================

    @GetMapping("/{id:\\d+}/tong-quan")
    public KhachHangTongQuanResponse tongQuan(@PathVariable("id") Integer id) {
        service.one(id); // ✅ đảm bảo KH tồn tại
        return thongKeService.tongQuan(id);
    }

    // ✅ Alias theo log FE
    @GetMapping("/{id:\\d+}/thong-ke")
    public KhachHangTongQuanResponse thongKeAlias1(@PathVariable("id") Integer id) {
        service.one(id);
        return thongKeService.tongQuan(id);
    }

    // ✅ Alias theo log FE: /khach-hang/thong-ke/{id}
    @GetMapping("/thong-ke/{id:\\d+}")
    public KhachHangTongQuanResponse thongKeAlias2(@PathVariable("id") Integer id) {
        service.one(id);
        return thongKeService.tongQuan(id);
    }

    // ✅ Alias theo log FE: /khach-hang/tong-quan/{id}
    @GetMapping("/tong-quan/{id:\\d+}")
    public KhachHangTongQuanResponse tongQuanAlias(@PathVariable("id") Integer id) {
        service.one(id);
        return thongKeService.tongQuan(id);
    }
}
