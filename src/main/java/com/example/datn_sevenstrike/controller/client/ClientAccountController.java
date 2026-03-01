package com.example.datn_sevenstrike.controller.client;

import com.example.datn_sevenstrike.dto.client.ChangePasswordRequest;
import com.example.datn_sevenstrike.dto.client.MyVoucherDTO;
import com.example.datn_sevenstrike.dto.request.DiaChiKhachHangRequest;
import com.example.datn_sevenstrike.dto.request.KhachHangRequest;
import com.example.datn_sevenstrike.dto.response.DiaChiKhachHangResponse;
import com.example.datn_sevenstrike.dto.response.KhachHangResponse;
import com.example.datn_sevenstrike.entity.KhachHang;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.exception.NotFoundEx;
import com.example.datn_sevenstrike.repository.KhachHangRepository;
import com.example.datn_sevenstrike.service.DiaChiKhachHangService;
import com.example.datn_sevenstrike.service.KhachHangService;
import com.example.datn_sevenstrike.service.FileStorageService;
import com.example.datn_sevenstrike.service.client.ClientOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/client/account")
@RequiredArgsConstructor
public class ClientAccountController {

    private final KhachHangService khachHangService;
    private final KhachHangRepository khachHangRepo;
    private final DiaChiKhachHangService diaChiService;
    private final FileStorageService fileStorageService;
    private final ClientOrderService clientOrderService;

    // ===== PROFILE =====

    @GetMapping("/profile")
    public ResponseEntity<KhachHangResponse> getProfile(@RequestParam Integer id) {
        return ResponseEntity.ok(khachHangService.one(id));
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<KhachHangResponse> updateProfile(@PathVariable Integer id, @RequestBody KhachHangRequest req) {
        return ResponseEntity.ok(khachHangService.update(id, req));
    }

    @PostMapping("/profile/{id}/avatar")
    public ResponseEntity<?> uploadAvatar(@PathVariable Integer id, @RequestParam("file") MultipartFile file) {
        KhachHang kh = khachHangRepo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy tài khoản"));

        // Delete old avatar if exists
        if (kh.getAnhDaiDien() != null) {
            fileStorageService.tryDeleteByPublicPath(kh.getAnhDaiDien());
        }

        String url = fileStorageService.saveImage(file, "khach_hang");
        kh.setAnhDaiDien(url);
        kh.setNgayCapNhat(LocalDateTime.now());
        khachHangRepo.save(kh);

        return ResponseEntity.ok(java.util.Map.of("anhDaiDien", url));
    }

    // ===== PASSWORD =====

    @PutMapping("/password/{id}")
    public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody ChangePasswordRequest req) {
        KhachHang kh = khachHangRepo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new NotFoundEx("Không tìm thấy tài khoản"));

        if (req.getMatKhauCu() == null || req.getMatKhauCu().trim().isEmpty()) {
            throw new BadRequestEx("Vui lòng nhập mật khẩu cũ!");
        }
        if (req.getMatKhauMoi() == null || req.getMatKhauMoi().trim().isEmpty()) {
            throw new BadRequestEx("Vui lòng nhập mật khẩu mới!");
        }

        if (!Objects.equals(kh.getMatKhau(), req.getMatKhauCu())) {
            throw new BadRequestEx("Mật khẩu cũ không chính xác!");
        }

        kh.setMatKhau(req.getMatKhauMoi());
        kh.setNgayCapNhat(LocalDateTime.now());
        khachHangRepo.save(kh);

        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }

    // ===== ADDRESSES =====

    @GetMapping("/addresses/{customerId}")
    public ResponseEntity<List<DiaChiKhachHangResponse>> getAddresses(@PathVariable Integer customerId) {
        return ResponseEntity.ok(diaChiService.byKhachHang(customerId));
    }

    @PostMapping("/addresses")
    public ResponseEntity<DiaChiKhachHangResponse> createAddress(@RequestBody DiaChiKhachHangRequest req) {
        return ResponseEntity.ok(diaChiService.create(req));
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<DiaChiKhachHangResponse> updateAddress(@PathVariable Integer id, @RequestBody DiaChiKhachHangRequest req) {
        return ResponseEntity.ok(diaChiService.update(id, req));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<?> deleteAddress(@PathVariable Integer id) {
        diaChiService.delete(id);
        return ResponseEntity.ok("Xóa địa chỉ thành công!");
    }

    // ===== COUPONS =====

    @GetMapping("/coupons")
    public ResponseEntity<List<MyVoucherDTO>> getMyCoupons(@RequestParam Integer customerId) {
        return ResponseEntity.ok(clientOrderService.getMyCoupons(customerId));
    }
}