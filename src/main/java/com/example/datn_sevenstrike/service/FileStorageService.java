package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.exception.BadRequestEx;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root = Paths.get("uploads");

    public String saveNhanVienAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestEx("File không phải ảnh");
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "avatar" : file.getOriginalFilename());
        String ext = "";

        int dot = original.lastIndexOf(".");
        if (dot >= 0) ext = original.substring(dot);

        String filename = UUID.randomUUID() + ext;
        Path dir = root.resolve("nhan_vien");
        Path target = dir.resolve(filename);

        try {
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BadRequestEx("Lưu file thất bại: " + e.getMessage());
        }

        // trả path public để FE dùng
        return "/uploads/nhan_vien/" + filename;
    }

    public void tryDeleteByPublicPath(String publicPath) {
        // publicPath dạng "/uploads/nhan_vien/xxx.png"
        if (publicPath == null || publicPath.isBlank()) return;
        String p = publicPath.trim();
        if (!p.startsWith("/uploads/")) return;

        // map "/uploads/..." -> "uploads/..."
        String relative = p.substring(1);
        Path file = Paths.get(relative).toAbsolutePath().normalize();

        try {
            Files.deleteIfExists(file);
        } catch (Exception ignored) {
        }
    }
}
