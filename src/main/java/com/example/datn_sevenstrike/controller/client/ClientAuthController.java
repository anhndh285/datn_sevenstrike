package com.example.datn_sevenstrike.controller.client;

import com.example.datn_sevenstrike.dto.client.ClientLoginResponse;
import com.example.datn_sevenstrike.dto.client.ClientRegisterRequest;
import com.example.datn_sevenstrike.dto.request.LoginRequest;
import com.example.datn_sevenstrike.service.client.ClientAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/client")
@RequiredArgsConstructor
public class ClientAuthController {

    private final ClientAuthService clientAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> clientLogin(@RequestBody LoginRequest req) {
        try {
            ClientLoginResponse response = clientAuthService.authenticateClient(req);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg == null || msg.trim().isEmpty()) {
                msg = "Đăng nhập thất bại, vui lòng thử lại.";
            }
            if (msg.toLowerCase().contains("khóa")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(msg);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> clientRegister(@RequestBody ClientRegisterRequest req) {
        try {
            ClientLoginResponse response = clientAuthService.registerClient(req);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg == null || msg.trim().isEmpty()) {
                msg = "Đăng ký thất bại, vui lòng thử lại.";
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
        }
    }
}