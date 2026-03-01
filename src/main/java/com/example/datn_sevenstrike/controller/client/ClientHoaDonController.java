package com.example.datn_sevenstrike.controller.client;

import com.example.datn_sevenstrike.dto.response.HoaDonChiTietResponse;
import com.example.datn_sevenstrike.dto.response.HoaDonResponse;
import com.example.datn_sevenstrike.entity.HoaDon;
import com.example.datn_sevenstrike.entity.HoaDonChiTiet;
import com.example.datn_sevenstrike.entity.ChiTietSanPham;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.HoaDonChiTietRepository;
import com.example.datn_sevenstrike.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/client/hoa-don")
@RequiredArgsConstructor
public class ClientHoaDonController {

    private final HoaDonRepository repo;
    private final HoaDonChiTietRepository hdctRepo;
    private final ModelMapper mapper;

    @GetMapping("/tracking")
    public HoaDonResponse trackOrder(
            @RequestParam(required = false) String maHoaDon,
            @RequestParam(required = false) Integer id,
            @RequestParam String email) {

        HoaDon hd;
        if (maHoaDon != null && !maHoaDon.isBlank()) {
            hd = repo.findByMaHoaDonAndXoaMemFalse(maHoaDon.trim())
                    .orElseThrow(() -> new NotFoundEx("Không tìm thấy đơn hàng"));
        } else if (id != null) {
            hd = repo.findByIdAndXoaMemFalse(id)
                    .orElseThrow(() -> new NotFoundEx("Không tìm thấy đơn hàng"));
        } else {
            throw new BadRequestEx("Vui lòng cung cấp mã đơn hàng hoặc id");
        }

        if (hd.getEmailKhachHang() == null || !hd.getEmailKhachHang().equalsIgnoreCase(email.trim())) {
            throw new BadRequestEx("Thông tin xác thực không chính xác (Email không khớp)");
        }

        HoaDonResponse response = mapper.map(hd, HoaDonResponse.class);

        List<HoaDonChiTiet> details = hdctRepo.findAllByIdHoaDonAndXoaMemFalseOrderByIdAsc(hd.getId());
        List<HoaDonChiTietResponse> chiTietList = details.stream().map(ct -> {
            HoaDonChiTietResponse dto = mapper.map(ct, HoaDonChiTietResponse.class);
            ChiTietSanPham ctsp = ct.getChiTietSanPham();
            if (ctsp != null && ctsp.getSanPham() != null) {
                dto.setTenSanPham(ctsp.getSanPham().getTenSanPham());
            }
            return dto;
        }).collect(Collectors.toList());

        response.setChiTietHoaDon(chiTietList);
        return response;
    }
}