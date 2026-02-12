package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.dto.request.MauSacRequest;
import com.example.datn_sevenstrike.dto.response.MauSacResponse;
import com.example.datn_sevenstrike.entity.MauSac;
import com.example.datn_sevenstrike.repository.MauSacRepository;
import java.security.SecureRandom;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class MauSacService {

    private final MauSacRepository repo;

    private final SecureRandom random = new SecureRandom();

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

        // ✅ Tự sinh HEX nếu không nhập
        if (hex == null) {
            hex = autoHexCreate(ten);
        }

        if (repo.existsByMaMauHexIgnoreCaseAndXoaMemFalse(hex)) {
            // Trường hợp cực hiếm do chạy song song, nhưng vẫn chặn
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

        // ✅ Phân biệt: FE không gửi maMauHex (null) => giữ HEX cũ
        String hex;
        if (req.getMaMauHex() == null) {
            hex = e.getMaMauHex();
            // Nếu dữ liệu cũ đang null thì tự sinh để đồng bộ hiển thị
            if (hex == null || hex.trim().isEmpty()) {
                hex = autoHexUpdate(ten, id);
            }
        } else {
            // FE có gửi field maMauHex (có thể rỗng)
            hex = normHex(req.getMaMauHex());
            if (hex != null && !isValidHex(hex)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mã màu HEX không hợp lệ (dạng #RRGGBB)");
            }
            // Nếu gửi rỗng => tự sinh HEX
            if (hex == null) {
                hex = autoHexUpdate(ten, id);
            }
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

    // ✅ Sinh HEX ổn định theo tên + đảm bảo không trùng
    private String autoHexCreate(String tenMauSac) {
        for (int i = 0; i < 30; i++) {
            String candidate = genHexFromSeed(tenMauSac + "|" + i);
            if (!repo.existsByMaMauHexIgnoreCaseAndXoaMemFalse(candidate)) {
                return candidate;
            }
        }
        // Fallback random (hiếm)
        for (int i = 0; i < 30; i++) {
            String candidate = randomHex();
            if (!repo.existsByMaMauHexIgnoreCaseAndXoaMemFalse(candidate)) {
                return candidate;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể tự sinh mã màu HEX. Vui lòng thử lại.");
    }

    private String autoHexUpdate(String tenMauSac, Integer idNot) {
        for (int i = 0; i < 30; i++) {
            String candidate = genHexFromSeed(tenMauSac + "|" + i);
            if (!repo.existsByMaMauHexIgnoreCaseAndXoaMemFalseAndIdNot(candidate, idNot)) {
                return candidate;
            }
        }
        for (int i = 0; i < 30; i++) {
            String candidate = randomHex();
            if (!repo.existsByMaMauHexIgnoreCaseAndXoaMemFalseAndIdNot(candidate, idNot)) {
                return candidate;
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không thể tự sinh mã màu HEX. Vui lòng thử lại.");
    }

    private String genHexFromSeed(String seed) {
        int rgb = seed.hashCode() & 0x00FFFFFF;

        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;

        // tránh quá tối/quá sáng để dot nhìn rõ
        int yiq = (r * 299 + g * 587 + b * 114) / 1000;
        if (yiq < 70) {
            r = Math.min(255, r + 80);
            g = Math.min(255, g + 80);
            b = Math.min(255, b + 80);
        } else if (yiq > 220) {
            r = Math.max(0, r - 60);
            g = Math.max(0, g - 60);
            b = Math.max(0, b - 60);
        }

        return String.format("#%02x%02x%02x", r, g, b);
    }

    private String randomHex() {
        int rgb = random.nextInt(0x1000000);
        return String.format("#%06x", rgb);
    }
}
