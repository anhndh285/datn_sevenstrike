// File: src/main/java/com/example/datn_sevenstrike/controller/ChiTietDotGiamGiaBanHangController.java
package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.response.ChiTietDotGiamGiaResponse;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.service.ChiTietDotGiamGiaService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/api/chi-tiet-dot-giam-gia")
@RequiredArgsConstructor
public class ChiTietDotGiamGiaBanHangController {

    private final ChiTietDotGiamGiaService service;

    // Lấy "đợt giảm giá tốt nhất" cho 1 CTSP
    @GetMapping("/ban-hang/ctsp/{idChiTietSanPham}")
    public ChiTietDotGiamGiaResponse bestForCtspBanHang(@PathVariable Integer idChiTietSanPham) {
        return service.bestForCtspBanHang(idChiTietSanPham);
    }

    // Batch: lấy "đợt giảm giá tốt nhất" cho nhiều CTSP
    // Chấp nhận 2 dạng payload:
    // 1) [1,2,3]
    // 2) { "idChiTietSanPhams": [1,2,3] }
    @PostMapping("/ban-hang/best-by-ctsp-ids")
    public List<ChiTietDotGiamGiaResponse> bestForCtspIdsBanHang(@RequestBody JsonNode body) {
        List<Integer> ids = parseIds(body);
        return service.bestForCtspIdsBanHang(ids);
    }

    private List<Integer> parseIds(JsonNode body) {
        List<Integer> ids = new ArrayList<>();

        if (body == null || body.isNull()) {
            throw new BadRequestEx("Dữ liệu gửi lên không hợp lệ.");
        }

        if (body.isArray()) {
            for (JsonNode n : body) {
                if (n != null && n.canConvertToInt()) {
                    ids.add(n.asInt());
                }
            }
        } else if (body.isObject() && body.has("idChiTietSanPhams") && body.get("idChiTietSanPhams").isArray()) {
            for (JsonNode n : body.get("idChiTietSanPhams")) {
                if (n != null && n.canConvertToInt()) {
                    ids.add(n.asInt());
                }
            }
        } else {
            throw new BadRequestEx("Dữ liệu gửi lên không đúng định dạng danh sách idChiTietSanPhams.");
        }

        if (ids.isEmpty()) {
            throw new BadRequestEx("Danh sách idChiTietSanPhams đang trống.");
        }

        return ids;
    }
}
