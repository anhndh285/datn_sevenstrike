// File: src/main/java/com/example/datn_sevenstrike/controller/HoaDonController.java
package com.example.datn_sevenstrike.controller;

import com.example.datn_sevenstrike.dto.request.HoaDonChiTietRequest;
import com.example.datn_sevenstrike.dto.request.HoaDonRequest;
import com.example.datn_sevenstrike.dto.request.XacNhanThanhToanTaiQuayRequest;
import com.example.datn_sevenstrike.dto.response.HoaDonResponse;
import com.example.datn_sevenstrike.exception.BadRequestEx;
import com.example.datn_sevenstrike.service.HoaDonService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
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

        String regex = "\"" + Pattern.quote(key) + "\"\\s*:\\s*\"?([^\",}\\s]+)\"?";
        Matcher m = Pattern.compile(regex).matcher(json);
        if (!m.find()) return null;

        return parseNhanVienIdRaw(m.group(1));
    }

    private String timChuoiTrongJson(String json, String key) {
        if (json == null || json.isBlank() || key == null || key.isBlank()) return null;

        String regex = "\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]*)\"";
        Matcher m = Pattern.compile(regex).matcher(json);
        if (!m.find()) return null;

        String v = m.group(1);
        return (v == null) ? null : v.trim();
    }

    private List<String> timDanhSachChuoiTrongJson(String json, String key) {
        List<String> out = new ArrayList<>();
        if (json == null || json.isBlank() || key == null || key.isBlank()) return out;

        String regex = "\"" + Pattern.quote(key) + "\"\\s*:\\s*\\[(.*?)\\]";
        Matcher m = Pattern.compile(regex, Pattern.DOTALL).matcher(json);
        if (!m.find()) return out;

        String inside = m.group(1);
        if (inside == null || inside.isBlank()) return out;

        Matcher m2 = Pattern.compile("\"([^\"]+)\"").matcher(inside);
        while (m2.find()) {
            String v = m2.group(1);
            if (v != null && !v.isBlank()) {
                out.add(v.trim());
            }
        }

        return out;
    }

    private String layTenTaiKhoanTuSecurityContext() {
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
        Integer id = layNhanVienIdTuHeaderCustom(request);
        if (id != null) return id;

        id = layNhanVienIdTuJwt(request);
        if (id != null) return id;

        String tk = layTenTaiKhoanTuSecurityContext();
        if (tk != null && !tk.isBlank()) {
            id = timNhanVienIdTheoTenTaiKhoan(tk);
            if (id != null) return id;
        }

        return null;
    }

    // =========================
    // ===== CHECK ADMIN =======
    // =========================

    private String normalizeRole(String role) {
        String r = String.valueOf(role == null ? "" : role).trim().toUpperCase();
        r = r.replace("ROLE_", "");
        r = r.replace("-", "_");
        r = r.replace(' ', '_');
        while (r.contains("__")) {
            r = r.replace("__", "_");
        }
        return r;
    }

    private boolean laAdminRole(String role) {
        String r = normalizeRole(role);
        return "ADMIN".equals(r)
                || "QUAN_LY".equals(r)
                || "QUANLY".equals(r)
                || "MANAGER".equals(r)
                || "ADMINISTRATOR".equals(r)
                || "SUPER_ADMIN".equals(r)
                || "SUPERADMIN".equals(r);
    }

    private Boolean coQuyenAdminTuHeaderRole(HttpServletRequest request) {
        if (request == null) return null;

        String[] keys = new String[] {
                "X-User-Role",
                "X-Role",
                "X-Vai-Tro",
                "X-Quyen-Han"
        };

        for (String k : keys) {
            String v = layHeader(request, k);
            if (v != null && !v.isBlank()) {
                return laAdminRole(v);
            }
        }

        return null;
    }

    private Boolean coQuyenAdminTuJwt(HttpServletRequest request) {
        String token = layBearerToken(request);
        if (token == null) return null;

        String json = decodeJwtPayloadJson(token);
        if (json == null || json.isBlank()) return null;

        String[] roleKeys = new String[] {
                "role",
                "vaiTro",
                "tenVaiTro",
                "tenQuyenHan",
                "authority",
                "quyenHan"
        };

        for (String k : roleKeys) {
            String v = timChuoiTrongJson(json, k);
            if (v != null && !v.isBlank()) {
                return laAdminRole(v);
            }
        }

        String[] arrayRoleKeys = new String[] {
                "roles",
                "authorities",
                "permissions",
                "grantedAuthorities"
        };

        for (String k : arrayRoleKeys) {
            List<String> values = timDanhSachChuoiTrongJson(json, k);
            for (String v : values) {
                if (laAdminRole(v)) {
                    return true;
                }
            }
        }

        return null;
    }

    private Boolean coQuyenAdminTuSecurityContext() {
        try {
            Class<?> ctxHolderClz = Class.forName("org.springframework.security.core.context.SecurityContextHolder");
            Object ctx = ctxHolderClz.getMethod("getContext").invoke(null);
            if (ctx == null) return null;

            Object auth = ctx.getClass().getMethod("getAuthentication").invoke(ctx);
            if (auth == null) return null;

            Object authoritiesObj = auth.getClass().getMethod("getAuthorities").invoke(auth);
            if (!(authoritiesObj instanceof Collection<?>)) {
                return null;
            }

            Collection<?> authorities = (Collection<?>) authoritiesObj;
            if (authorities.isEmpty()) {
                return null;
            }

            for (Object authority : authorities) {
                String value;
                try {
                    Object x = authority.getClass().getMethod("getAuthority").invoke(authority);
                    value = x == null ? "" : String.valueOf(x);
                } catch (Exception e) {
                    value = String.valueOf(authority);
                }

                if (laAdminRole(value)) return true;
            }

            return false;
        } catch (Exception e) {
            return null;
        }
    }

    private void yeuCauQuyenAdmin(HttpServletRequest request) {
        String authHeader = layHeader(request, "Authorization");
        String roleHeader = layHeader(request, "X-User-Role");
        String roleHeader2 = layHeader(request, "X-Role");
        String token = layBearerToken(request);
        String payload = decodeJwtPayloadJson(token);

        Boolean byHeaderRole = coQuyenAdminTuHeaderRole(request);
        Boolean byJwt = coQuyenAdminTuJwt(request);
        Boolean bySecurity = coQuyenAdminTuSecurityContext();

        System.out.println("=== CHECK ADMIN HOA DON ===");
        System.out.println("Authorization header = " + authHeader);
        System.out.println("X-User-Role = " + roleHeader);
        System.out.println("X-Role = " + roleHeader2);
        System.out.println("JWT payload = " + payload);
        System.out.println("byHeaderRole = " + byHeaderRole);
        System.out.println("byJwt = " + byJwt);
        System.out.println("bySecurity = " + bySecurity);
        System.out.println("===========================");

        if (Boolean.TRUE.equals(byHeaderRole) || Boolean.TRUE.equals(byJwt) || Boolean.TRUE.equals(bySecurity)) {
            return;
        }

        throw new BadRequestEx("Chỉ Admin/Quản lý mới được thực hiện thao tác này");
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

    // =========================
    // ===== API gộp từ Duy ====
    // =========================

    @PostMapping("/{id}/xac-nhan-huy-theo-yeu-cau")
    public HoaDonResponse xacNhanHuyTheoYeuCau(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestBody(required = false) HuyDonBody body
    ) {
        yeuCauQuyenAdmin(request);

        Integer nhanVienId = layNhanVienIdTuRequest(request);
        String lyDo = body == null ? null : body.getLyDo();
        return service.adminConfirmCancel(id, nhanVienId, lyDo);
    }

    @PostMapping("/{id}/tu-choi-huy")
    public HoaDonResponse tuChoiHuy(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestBody(required = false) HuyDonBody body
    ) {
        yeuCauQuyenAdmin(request);

        Integer nhanVienId = layNhanVienIdTuRequest(request);
        String lyDo = body == null ? null : body.getLyDo();
        return service.adminRejectCancel(id, nhanVienId, lyDo);
    }

    @PutMapping("/{id}/thong-tin-giao-hang")
    public HoaDonResponse updateThongTinGiaoHang(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestBody ThongTinGiaoHangBody body
    ) {
        Integer nhanVienId = layNhanVienIdTuRequest(request);

        return service.adminUpdateThongTinGiaoHang(
                id,
                body.getTenKhachHang(),
                body.getSoDienThoaiKhachHang(),
                body.getEmailKhachHang(),
                body.getDiaChiKhachHang(),
                nhanVienId
        );
    }

    @PostMapping("/{id}/xac-nhan-hoan-phi")
    public HoaDonResponse xacNhanHoanPhi(
            HttpServletRequest request,
            @PathVariable Integer id,
            @RequestBody(required = false) HoanPhiBody body
    ) {
        yeuCauQuyenAdmin(request);

        Integer nhanVienId = layNhanVienIdTuRequest(request);
        return service.confirmHoanPhi(id, nhanVienId);
    }

    @GetMapping("/can-hoan-phi")
    public List<HoaDonResponse> getDonCanHoanPhi(HttpServletRequest request) {
        yeuCauQuyenAdmin(request);
        return service.getDonCanHoanPhi();
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

    @Data
    public static class HuyDonBody {
        private Integer nhanVienId;
        private String lyDo;
    }

    @Data
    public static class ThongTinGiaoHangBody {
        private String tenKhachHang;
        private String soDienThoaiKhachHang;
        private String emailKhachHang;
        private String diaChiKhachHang;
    }

    @Data
    public static class HoanPhiBody {
        private Integer nhanVienId;
    }
}