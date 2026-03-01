// File: src/main/java/com/example/datn_sevenstrike/controller/HoaDonController.java
package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.HoaDonChiTietRequest;
import com.example.datn_sevenstrike.dto.request.HoaDonRequest;
import com.example.datn_sevenstrike.dto.request.XacNhanThanhToanTaiQuayRequest;
import com.example.datn_sevenstrike.dto.response.HoaDonResponse;
import com.example.datn_sevenstrike.service.HoaDonService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/hoa-don")
@RequiredArgsConstructor
public class HoaDonController {

    private final HoaDonService service;

    @PersistenceContext
    private EntityManager em;

    private Integer parseNhanVienIdRaw(String raw) {
        if (raw == null || raw.isBlank()) return null;

        // ✅ robust: "2" / " 2 " / "NV00002" đều ra 2
        String digits = raw.replaceAll("\\D", "");
        if (digits.isBlank()) return null;

        try {
            return Integer.valueOf(digits);
        } catch (Exception e) {
            return null;
        }
    }

    private String layHeader(HttpServletRequest request, String key) {
        if (request == null || key == null) return null;

        String v = request.getHeader(key);
        if (v != null && !v.isBlank()) return v;

        v = request.getHeader(key.toLowerCase());
        if (v != null && !v.isBlank()) return v;

        return null;
    }

    private Integer timNhanVienIdTheoTenTaiKhoan(String tenTaiKhoan) {
        try {
            if (tenTaiKhoan == null) return null;
            String tk = tenTaiKhoan.trim();
            if (tk.isBlank()) return null;

            String sql = """
                    select top 1 nv.id
                    from dbo.nhan_vien nv
                    where nv.xoa_mem = 0
                      and nv.ten_tai_khoan = :tk
                    """;

            Object one = em.createNativeQuery(sql)
                    .setParameter("tk", tk)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (one == null) return null;
            return ((Number) one).intValue();
        } catch (Exception e) {
            return null;
        }
    }

    // =========================
    // ========== JWT ==========
    // =========================

    private String layBearerToken(HttpServletRequest request) {
        if (request == null) return null;

        String auth = layHeader(request, "Authorization");
        if (auth == null || auth.isBlank()) return null;

        String token = auth.trim();
        if (token.toLowerCase().startsWith("bearer ")) {
            token = token.substring(7).trim();
        }

        if (token.isBlank() || !token.contains(".")) return null;
        return token;
    }

    private String decodeJwtPayloadJson(String token) {
        try {
            if (token == null || token.isBlank()) return null;

            String[] parts = token.split("\\.");
            if (parts.length < 2) return null;

            byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    private Integer timSoTrongJson(String json, String key) {
        if (json == null || json.isBlank() || key == null || key.isBlank()) return null;

        // "idNhanVien": 2 | "idNhanVien":"2" | "idNhanVien":"NV00002"
        String regex = "\"" + Pattern.quote(key) + "\"\\s*:\\s*\"?([^\",}\\s]+)\"?";
        Matcher m = Pattern.compile(regex).matcher(json);
        if (!m.find()) return null;

        return parseNhanVienIdRaw(m.group(1));
    }

    private String timChuoiTrongJson(String json, String key) {
        if (json == null || json.isBlank() || key == null || key.isBlank()) return null;

        // "username":"admin"
        String regex = "\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"";
        Matcher m = Pattern.compile(regex).matcher(json);
        if (!m.find()) return null;

        String v = m.group(1);
        return (v == null) ? null : v.trim();
    }

    private String layTenTaiKhoanTuSecurityContext() {
        // ✅ dùng reflection để KHÔNG phụ thuộc spring-security compile-time
        try {
            Class<?> ctxHolderClz = Class.forName("org.springframework.security.core.context.SecurityContextHolder");
            Object ctx = ctxHolderClz.getMethod("getContext").invoke(null);
            if (ctx == null) return null;

            Object auth = ctx.getClass().getMethod("getAuthentication").invoke(ctx);
            if (auth == null) return null;

            Object name = auth.getClass().getMethod("getName").invoke(auth);
            if (name == null) return null;

            String s = String.valueOf(name).trim();
            if (s.isBlank() || "anonymousUser".equalsIgnoreCase(s)) return null;
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    private Integer layNhanVienIdTuHeaderCustom(HttpServletRequest request) {
        if (request == null) return null;

        // ✅ ưu tiên id trực tiếp
        String[] keysId = new String[] {
                "X-Nhan-Vien-Id",
                "X-NV-ID",
                "X-NhanVienId",
                "X-Employee-Id",
                "X-Ma-Nhan-Vien"
        };

        for (String k : keysId) {
            Integer id = parseNhanVienIdRaw(layHeader(request, k));
            if (id != null) return id;
        }

        // ✅ fallback theo tên tài khoản nếu FE có gửi
        String tk = layHeader(request, "X-Ten-Tai-Khoan");
        if (tk != null && !tk.isBlank()) {
            Integer id = timNhanVienIdTheoTenTaiKhoan(tk);
            if (id != null) return id;
        }

        return null;
    }

    private Integer layNhanVienIdTuJwt(HttpServletRequest request) {
        String token = layBearerToken(request);
        if (token == null) return null;

        String json = decodeJwtPayloadJson(token);
        if (json == null || json.isBlank()) return null;

        // 1) cố lấy id dạng số trước
        String[] keyId = new String[] {
                "idNhanVien",
                "nhanVienId",
                "id_nhan_vien",
                "nvId",
                "nv_id",
                "employeeId",
                "empId",
                "userId",
                "uid",
                "id"
        };

        for (String k : keyId) {
            Integer id = timSoTrongJson(json, k);
            if (id != null) return id;
        }

        // 2) fallback: mã NV
        String[] keyMa = new String[] {
                "maNhanVien",
                "ma_nhan_vien",
                "maNV",
                "ma_nv"
        };

        for (String k : keyMa) {
            Integer id = timSoTrongJson(json, k);
            if (id != null) return id;
        }

        // 3) fallback: username/sub -> query DB để ra id
        String[] keyTk = new String[] {
                "tenTaiKhoan",
                "ten_tai_khoan",
                "username",
                "taiKhoan",
                "account",
                "sub"
        };

        for (String k : keyTk) {
            String tk = timChuoiTrongJson(json, k);
            if (tk == null || tk.isBlank()) continue;

            Integer id = timNhanVienIdTheoTenTaiKhoan(tk);
            if (id != null) return id;
        }

        return null;
    }

    private Integer layNhanVienIdTuRequest(HttpServletRequest request) {
        // 1) header custom
        Integer id = layNhanVienIdTuHeaderCustom(request);
        if (id != null) return id;

        // 2) jwt Authorization
        id = layNhanVienIdTuJwt(request);
        if (id != null) return id;

        // 3) security context -> query DB theo username
        String tk = layTenTaiKhoanTuSecurityContext();
        if (tk != null && !tk.isBlank()) {
            id = timNhanVienIdTheoTenTaiKhoan(tk);
            if (id != null) return id;
        }

        return null;
    }

    // =========================================================
    // ========================= API ============================
    // =========================================================

    @GetMapping
    public List<HoaDonResponse> all() {
        return service.all();
    }

    @GetMapping("/page")
    public Page<HoaDonResponse> page(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize
    ) {
        return service.page(pageNo, pageSize);
    }

    @GetMapping("/{id}")
    public HoaDonResponse one(@PathVariable Integer id) {
        return service.one(id);
    }

    @GetMapping("/{id}/lich-su-thao-tac")
    public List<HoaDonService.LichSuThaoTacView> lichSuThaoTac(@PathVariable Integer id) {
        return service.lichSuThaoTac(id);
    }

    @GetMapping("/{id}/lich-su-thanh-toan")
    public List<HoaDonService.LichSuThanhToanView> lichSuThanhToan(@PathVariable Integer id) {
        return service.lichSuThanhToan(id);
    }

    @PostMapping
    public HoaDonResponse create(HttpServletRequest request, @RequestBody HoaDonRequest req) {
        Integer nguoiCapNhat = layNhanVienIdTuRequest(request);
        return service.create(req, nguoiCapNhat);
    }

    @PutMapping("/{id}/thong-tin")
    public HoaDonResponse capNhatThongTin(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestBody HoaDonRequest req
    ) {
        Integer nguoiCapNhat = layNhanVienIdTuRequest(request);
        return service.capNhatThongTinPos(id, req, nguoiCapNhat);
    }

    @PostMapping("/{id}/chi-tiet")
    public HoaDonResponse upsertChiTiet(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestBody List<HoaDonChiTietRequest> items
    ) {
        Integer nguoiCapNhat = layNhanVienIdTuRequest(request);
        return service.upsertChiTiet(id, items, nguoiCapNhat);
    }

    @PutMapping("/{id}/confirm-tai-quay-tien-mat")
    public HoaDonResponse confirmTaiQuayTienMat(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestBody(required = false) NoteBody body
    ) {
        Integer nguoiCapNhat = layNhanVienIdTuRequest(request);
        String note = body == null ? null : body.getGhiChu();
        return service.confirmTaiQuayTienMat(id, note, nguoiCapNhat);
    }

    @PutMapping("/{id}/confirm-tai-quay-chuyen-khoan")
    public HoaDonResponse confirmTaiQuayChuyenKhoan(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestBody(required = false) NoteBody body
    ) {
        Integer nguoiCapNhat = layNhanVienIdTuRequest(request);
        String note = body == null ? null : body.getGhiChu();
        return service.confirmTaiQuayChuyenKhoan(id, note, nguoiCapNhat);
    }

    @PutMapping("/{id}/confirm-tai-quay-ket-hop")
    public HoaDonResponse confirmTaiQuayKetHop(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestBody XacNhanThanhToanTaiQuayRequest body
    ) {
        Integer nguoiCapNhat = layNhanVienIdTuRequest(request);
        return service.confirmTaiQuayKetHop(id, body, nguoiCapNhat);
    }

    // ✅ NEW: xác nhận thanh toán cho đơn giao hàng/online (tạo giao_dich_thanh_toan để FE hiển thị lịch sử thanh toán)
    @PutMapping("/{id}/confirm-giao-hang-ket-hop")
    public HoaDonResponse confirmGiaoHangKetHop(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestBody XacNhanThanhToanTaiQuayRequest body
    ) {
        Integer nguoiCapNhat = layNhanVienIdTuRequest(request);
        return service.confirmGiaoHangKetHop(id, body, nguoiCapNhat);
    }

    @PutMapping("/{id}/trang-thai")
    public HoaDonResponse changeStatus(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestBody(required = false) ChangeStatusBody body
    ) {
        if (body == null) body = new ChangeStatusBody();

        Integer nguoiCapNhat = layNhanVienIdTuRequest(request);

        return service.changeStatus(id, body.getTrangThai(), body.getGhiChu(), nguoiCapNhat);
    }

    @DeleteMapping("/{id}/reset")
    public void reset(@PathVariable Integer id) {
        service.resetHoaDonChoTaiQuay(id);
    }

    @DeleteMapping("/{id}")
    public void delete(HttpServletRequest request, @PathVariable Integer id) {
        Integer nguoiCapNhat = layNhanVienIdTuRequest(request);
        service.delete(id, nguoiCapNhat);
    }

    @Data
    public static class ChangeStatusBody {
        private Integer trangThai;
        private String ghiChu;
    }

    @Data
    public static class NoteBody {
        private String ghiChu;
    }
}