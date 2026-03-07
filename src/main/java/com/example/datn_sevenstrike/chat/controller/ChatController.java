package com.example.datn_sevenstrike.chat.controller;

import com.example.datn_sevenstrike.chat.dto.DongPhienRequest;
import com.example.datn_sevenstrike.chat.dto.GuiTinNhanRequest;
import com.example.datn_sevenstrike.chat.dto.KhoiTaoPhienRequest;
import com.example.datn_sevenstrike.chat.dto.PhienChatDTO;
import com.example.datn_sevenstrike.chat.dto.TinNhanDTO;
import com.example.datn_sevenstrike.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // ─── WebSocket: Khách gửi tin nhắn ──────────────────────────────────────
    @MessageMapping("/chat/{id}/send")
    public void khachGuiTin(@DestinationVariable Integer id,
                            @Payload GuiTinNhanRequest req) {
        chatService.xuLyTinNhanKhach(id, req);
    }

    // ─── WebSocket: Nhân viên gửi tin nhắn ──────────────────────────────────
    @MessageMapping("/chat/{id}/nhanvien/send")
    public void nhanVienGuiTin(@DestinationVariable Integer id,
                               @Payload GuiTinNhanRequest req) {
        chatService.nhanVienGuiTin(id, req);
    }

    // ─── WebSocket: Đóng phiên ──────────────────────────────────────────────
    @MessageMapping("/chat/{id}/close")
    public void dongPhienWs(@DestinationVariable Integer id) {
        chatService.dongPhien(id);
    }

    // ─── REST: Khởi tạo phiên mới ───────────────────────────────────────────
    @PostMapping("/api/chat/start")
    public ResponseEntity<PhienChatDTO> khoiTaoPhien(@RequestBody KhoiTaoPhienRequest req) {
        log.info("[CHAT_CONTROLLER] POST /api/chat/start - tenKhach={}, khachHangId={}, loai={}, nhanVienId={}",
                req != null ? req.getTenKhach() : null,
                req != null ? req.getKhachHangId() : null,
                req != null ? req.getLoai() : null,
                req != null ? req.getNhanVienId() : null);

        PhienChatDTO dto = chatService.khoiTaoPhien(req);
        return ResponseEntity.ok(dto);
    }

    // ─── REST: Nhân viên nhận phiên ─────────────────────────────────────────
    @PostMapping("/api/chat/{id}/accept")
    public ResponseEntity<PhienChatDTO> nhanPhien(@PathVariable Integer id,
                                                  @RequestBody Map<String, Integer> body) {
        Integer nhanVienId = body.get("nhanVienId");
        return ResponseEntity.ok(chatService.nhanVienNhanPhien(id, nhanVienId));
    }

    // ─── REST: Đóng phiên (có kiểm tra quyền) ───────────────────────────────
    @PostMapping("/api/chat/{id}/close")
    public ResponseEntity<Void> dongPhien(@PathVariable Integer id,
                                          @RequestBody(required = false) DongPhienRequest req) {
        chatService.dongPhien(id, req);
        return ResponseEntity.ok().build();
    }

    // ─── REST: Nhân viên khởi tạo phiên nội bộ ──────────────────────────────
    @PostMapping("/api/chat/staff/start")
    public ResponseEntity<PhienChatDTO> khoiTaoPhienNoiBo(@RequestBody KhoiTaoPhienRequest req) {
        req.setLoai("NOI_BO");
        PhienChatDTO dto = chatService.khoiTaoPhien(req);
        return ResponseEntity.ok(dto);
    }

    // ─── REST: Danh sách phiên (admin) ──────────────────────────────────────
    @GetMapping("/api/chat/sessions")
    public ResponseEntity<List<PhienChatDTO>> layDanhSachPhien(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String loai) {
        return ResponseEntity.ok(chatService.layDanhSachPhien(trangThai, loai));
    }

    // ─── REST: Lấy thông tin một phiên ──────────────────────────────────────
    @GetMapping("/api/chat/{id}")
    public ResponseEntity<PhienChatDTO> layPhien(@PathVariable Integer id) {
        return chatService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── REST: Lịch sử tin nhắn ─────────────────────────────────────────────
    @GetMapping("/api/chat/{id}/messages")
    public ResponseEntity<List<TinNhanDTO>> layTinNhan(@PathVariable Integer id) {
        return ResponseEntity.ok(chatService.layTinNhan(id));
    }
}