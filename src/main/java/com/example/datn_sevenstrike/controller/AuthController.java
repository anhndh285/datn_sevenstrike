package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.LoginRequest;
import com.example.datn_sevenstrike.dto.response.LoginResponse;
import com.example.datn_sevenstrike.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            LoginResponse response = authService.authenticate(req);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg == null || msg.trim().isEmpty()) {
                msg = "Đăng nhập thất bại, vui lòng thử lại.";
            }

            String msgLower = msg.toLowerCase();

            if (msgLower.contains("khóa")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Hệ thống đang gặp lỗi, vui lòng thử lại sau.");
        }
    }
}
