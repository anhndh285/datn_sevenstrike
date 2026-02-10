package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.exception.BadRequestEx;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String saveNhanVienAvatar(MultipartFile file) {
        return saveImage(file, "nhan_vien");
    }

    /**
     * Save ảnh vào: {uploadDir}/{folder}/{uuid}.{ext}
     * Trả public path: /uploads/{folder}/{uuid}.{ext}
     */
    public String saveImage(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) return null;

        ensureImage(file);

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
        String ext = "";

        int dot = original.lastIndexOf(".");
        if (dot >= 0 && dot < original.length() - 1) ext = original.substring(dot);

        String filename = UUID.randomUUID().toString().replace("-", "") + ext;

        Path dir = Paths.get(uploadDir, folder).toAbsolutePath().normalize();
        Path target = dir.resolve(filename);

        try {
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BadRequestEx("Lưu file thất bại: " + e.getMessage());
        }

        return "/uploads/" + folder + "/" + filename;
    }

    public void tryDeleteByPublicPath(String publicPath) {
        if (publicPath == null || publicPath.isBlank()) return;

        String p = publicPath.trim();
        if (!p.startsWith("/uploads/")) return;

        // /uploads/...  ->  {uploadDir}/...
        String relative = p.substring("/uploads/".length()); // e.g. "nhan_vien/xxx.png"
        Path file = Paths.get(uploadDir, relative).toAbsolutePath().normalize();

        try {
            Files.deleteIfExists(file);
        } catch (Exception ignored) {
        }
    }

    private void ensureImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
            throw new BadRequestEx("File không phải ảnh");
        }
    }
}
