package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.MauSacRequest;
import com.example.datn_sevenstrike.dto.response.MauSacResponse;
import com.example.datn_sevenstrike.entity.MauSac;
import com.example.datn_sevenstrike.repository.MauSacRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MauSacService {

    private final MauSacRepository repo;

    public List<MauSacResponse> all() {
        return repo.findAllByXoaMemFalseOrderByIdDesc().stream().map(this::toRes).toList();
    }

    public List<MauSacResponse> allActive() {
        return repo.findAllByTrangThaiTrueAndXoaMemFalseOrderByIdDesc().stream().map(this::toRes).toList();
    }

    public MauSacResponse one(Integer id) {
        MauSac e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy màu sắc"));
        return toRes(e);
    }

    public MauSacResponse create(MauSacRequest req) {
        String ten = normTen(req.getTenMauSac());
        if (ten.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên màu không được để trống");
        }

        String hex = normHex(req.getMaMauHex()); // null hoặc #rrggbb
        if (hex != null && !isValidHex(hex)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã màu HEX không hợp lệ (dạng #RRGGBB)");
        }

        if (repo.existsByTenMauSacIgnoreCaseAndXoaMemFalse(ten)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên màu đã tồn tại");
        }
        if (hex != null && repo.existsByMaMauHexIgnoreCaseAndXoaMemFalse(hex)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã màu HEX đã tồn tại");
        }

        MauSac e = new MauSac();
        e.setTenMauSac(ten);
        e.setMaMauHex(hex);
        e.setTrangThai(req.getTrangThai() != null ? req.getTrangThai() : true);
        e.setXoaMem(false);

        e = repo.save(e);
        return toRes(e);
    }

    public MauSacResponse update(Integer id, MauSacRequest req) {
        MauSac e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy màu sắc"));

        String ten = normTen(req.getTenMauSac());
        if (ten.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên màu không được để trống");
        }

        String hex = normHex(req.getMaMauHex());
        if (hex != null && !isValidHex(hex)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã màu HEX không hợp lệ (dạng #RRGGBB)");
        }

        if (repo.existsByTenMauSacIgnoreCaseAndXoaMemFalseAndIdNot(ten, id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tên màu đã tồn tại");
        }
        if (hex != null && repo.existsByMaMauHexIgnoreCaseAndXoaMemFalseAndIdNot(hex, id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã màu HEX đã tồn tại");
        }

        e.setTenMauSac(ten);
        e.setMaMauHex(hex);
        if (req.getTrangThai() != null) {
            e.setTrangThai(req.getTrangThai());
        }

        e = repo.save(e);
        return toRes(e);
    }

    public void delete(Integer id) {
        MauSac e = repo.findByIdAndXoaMemFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy màu sắc"));
        e.setXoaMem(true);
        repo.save(e);
    }

    // ====== helpers ======
    private MauSacResponse toRes(MauSac e) {
        return new MauSacResponse(
                e.getId(),
                e.getMaMauSac(),
                e.getTenMauSac(),
                e.getMaMauHex(),
                e.getTrangThai(),
                e.getXoaMem()
        );
    }

    private String normTen(String s) {
        return s == null ? "" : s.trim();
    }

    private String normHex(String s) {
        if (s == null) return null;
        String x = s.trim();
        if (x.isEmpty()) return null;
        if (!x.startsWith("#")) x = "#" + x;
        return x.toLowerCase();
    }

    private boolean isValidHex(String hex) {
        return hex.matches("^#[0-9a-fA-F]{6}$");
    }
}
