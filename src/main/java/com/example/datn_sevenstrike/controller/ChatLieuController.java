package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.ChatLieuRequest;
import com.example.datn_sevenstrike.dto.response.ChatLieuResponse;
import com.example.datn_sevenstrike.service.ChatLieuService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/chat-lieu")
@RequiredArgsConstructor
public class ChatLieuController {

    private final ChatLieuService service;

    @GetMapping
    public List<ChatLieuResponse> all() {
        return service.all();
    }

    @GetMapping("/{id}")
    public ChatLieuResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @PostMapping
    public ChatLieuResponse create(@RequestBody ChatLieuRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public ChatLieuResponse update(@PathVariable Integer id, @RequestBody ChatLieuRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
