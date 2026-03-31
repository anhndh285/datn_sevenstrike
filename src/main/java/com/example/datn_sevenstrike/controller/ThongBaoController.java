package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.response.ThongBaoResponse;
import com.example.datn_sevenstrike.dto.response.ThongBaoTongQuanResponse;
import com.example.datn_sevenstrike.entity.NhanVien;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.repository.NhanVienRepository;
import com.example.datn_sevenstrike.service.ThongBaoService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/thong-bao")
@RequiredArgsConstructor
public class ThongBaoController {

    private final ThongBaoService thongBaoService;
    private final NhanVienRepository nhanVienRepository;

    private Integer resolveNhanVienId(HttpServletRequest request, Integer nhanVienIdParam) {
        if (nhanVienIdParam != null) return nhanVienIdParam;

        if (request != null) {
            String rawId = request.getHeader("X-Nhan-Vien-Id");
            if (rawId == null || rawId.isBlank()) {
                rawId = request.getHeader("X-NV-ID");
            }
            if (rawId != null && !rawId.isBlank()) {
                try {
                    return Integer.parseInt(rawId.replaceAll("\\D", ""));
                } catch (Exception ignored) {
                }
            }

            String tenTaiKhoan = request.getHeader("X-Ten-Tai-Khoan");
            if (tenTaiKhoan != null && !tenTaiKhoan.isBlank()) {
                NhanVien nv = nhanVienRepository.findByTenTaiKhoanAndXoaMemFalse(tenTaiKhoan.trim()).orElse(null);
                if (nv != null) {
                    return nv.getId();
                }
            }
        }

        throw new BadRequestEx("Thiếu id nhân viên để lấy thông báo");
    }

    @GetMapping
    public List<ThongBaoResponse> layDanhSach(
            HttpServletRequest request,
            @RequestParam(required = false) Integer nhanVienId,
            @RequestParam(required = false, defaultValue = "false") Boolean chiLayChuaDoc,
            @RequestParam(required = false, defaultValue = "30") Integer gioiHan
    ) {
        Integer idNhanVien = resolveNhanVienId(request, nhanVienId);
        return thongBaoService.layDanhSach(idNhanVien, chiLayChuaDoc, gioiHan);
    }

    @GetMapping("/tong-quan")
    public ThongBaoTongQuanResponse layTongQuan(
            HttpServletRequest request,
            @RequestParam(required = false) Integer nhanVienId
    ) {
        Integer idNhanVien = resolveNhanVienId(request, nhanVienId);
        return thongBaoService.layTongQuan(idNhanVien);
    }

    @PostMapping("/{id}/doc")
    public ThongBaoResponse danhDauDaDoc(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestParam(required = false) Integer nhanVienId
    ) {
        Integer idNhanVien = resolveNhanVienId(request, nhanVienId);
        return thongBaoService.danhDauDaDoc(id, idNhanVien);
    }

    @PostMapping("/doc-tat-ca")
    public ThongBaoTongQuanResponse danhDauTatCaDaDoc(
            HttpServletRequest request,
            @RequestParam(required = false) Integer nhanVienId
    ) {
        Integer idNhanVien = resolveNhanVienId(request, nhanVienId);
        thongBaoService.danhDauTatCaDaDoc(idNhanVien);
        return thongBaoService.layTongQuan(idNhanVien);
    }

    @PostMapping("/{id}/xu-ly")
    public ThongBaoResponse danhDauDaXuLy(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestParam(required = false) Integer nhanVienId
    ) {
        Integer idNhanVien = resolveNhanVienId(request, nhanVienId);
        return thongBaoService.danhDauDaXuLy(id, idNhanVien);
    }
}
