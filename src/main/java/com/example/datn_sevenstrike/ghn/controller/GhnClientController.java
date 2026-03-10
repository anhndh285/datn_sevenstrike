package com.example.datn_sevenstrike.ghn.controller;

import com.example.datn_sevenstrike.ghn.dto.request.GhnTinhPhiRequest;
import com.example.datn_sevenstrike.ghn.dto.response.GhnDistrictResponse;
import com.example.datn_sevenstrike.ghn.dto.response.GhnProvinceResponse;
import com.example.datn_sevenstrike.ghn.dto.response.GhnTinhPhiResponse;
import com.example.datn_sevenstrike.ghn.dto.response.GhnWardResponse;
import com.example.datn_sevenstrike.ghn.service.GhnService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client/ghn")
public class GhnClientController {

    private final GhnService ghnService;

    @GetMapping("/tinh-thanh")
    public List<GhnProvinceResponse> getTinhThanh() {
        return ghnService.layDanhSachTinhThanh();
    }

    @GetMapping("/quan-huyen/{provinceId}")
    public List<GhnDistrictResponse> getQuanHuyen(@PathVariable Integer provinceId) {
        return ghnService.layDanhSachQuanHuyen(provinceId);
    }

    @GetMapping("/phuong-xa/{districtId}")
    public List<GhnWardResponse> getPhuongXa(@PathVariable Integer districtId) {
        return ghnService.layDanhSachPhuongXa(districtId);
    }

    @PostMapping("/tinh-phi-van-chuyen")
    public GhnTinhPhiResponse tinhPhiVanChuyen(@RequestBody GhnTinhPhiRequest request) {
        return ghnService.tinhPhiVanChuyen(request);
    }
}