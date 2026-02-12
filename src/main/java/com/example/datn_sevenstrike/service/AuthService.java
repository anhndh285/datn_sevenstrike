package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.LoginRequest;
import com.example.datn_sevenstrike.dto.response.LoginResponse;
import com.example.datn_sevenstrike.entity.NhanVien;
import com.example.datn_sevenstrike.repository.NhanVienRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final NhanVienRepository nhanVienRepo;

    public LoginResponse authenticate(LoginRequest req) {
        if (req == null) {
            throw new RuntimeException("Dữ liệu đăng nhập không hợp lệ!");
        }

        String username = req.getUsername();
        String password = req.getPassword();

        if (username == null || username.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập tài khoản!");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new RuntimeException("Vui lòng nhập mật khẩu!");
        }

        NhanVien nv = nhanVienRepo.findByTenTaiKhoanAndXoaMemFalse(username.trim())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại trên hệ thống!"));

        // ✅ Tránh NPE nếu nv.getMatKhau() null
        if (!Objects.equals(nv.getMatKhau(), password)) {
            throw new RuntimeException("Mật khẩu không chính xác!");
        }

        if (nv.getTrangThai() == null || !nv.getTrangThai()) {
            throw new RuntimeException("Tài khoản này hiện đang bị khóa!");
        }

        String role = "STAFF";
        String tenQuyen = "Nhân viên";

        // ✅ So sánh Integer chuẩn, tránh dùng ==
        if (Objects.equals(nv.getIdQuyenHan(), 1)) {
            role = "ADMIN";
            tenQuyen = "Quản trị viên";
        }

        return LoginResponse.builder()
                .id(nv.getId())
                .hoTen(nv.getTenNhanVien())
                .role(role)
                .message("Đăng nhập thành công với quyền " + tenQuyen)
                .build();
    }
}
