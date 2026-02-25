package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.entity.KhachHang;
import com.example.datn_sevenstrike.entity.NhanVien;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaiKhoanEmailService {

    private final JavaMailSender mailSender;

    // ✅ 2 template tách riêng
    private static final String TEMPLATE_NHAN_VIEN_PATH = "templates/taikhoan_nhan_vien_template.html";
    private static final String TEMPLATE_KHACH_HANG_PATH = "templates/taikhoan_khach_hang_template.html";

    // ✅ File phải nằm tại: src/main/resources/static/logo.png
    private static final String LOGO_PATH = "static/logo.png";

    // ✅ Link đăng nhập lấy từ properties (có default để khỏi lỗi)
    // Gợi ý cấu hình: app.link.dang-nhap=http://localhost:5173/login
    @Value("${app.link.dang-nhap:http://localhost:5173/login}")
    private String linkDangNhap;

    public void sendNhanVienEmail(String toEmail, NhanVien nhanVien, String rawPassword) {
        if (toEmail == null || toEmail.isBlank()) return;
        try {
            String subject = "[SevenStrike] Thông tin tài khoản nhân viên mới";
            String html = renderNhanVienTemplate(nhanVien, rawPassword);

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(toEmail.trim());
            helper.setSubject(subject);
            helper.setText(html, true);

            addInlineLogo(helper);

            mailSender.send(message);
            log.info("Đã gửi mail welcome cho nhân viên: {}", nhanVien.getTenTaiKhoan());

        } catch (Exception e) {
            log.error("Lỗi khi gửi mail nhân viên (ID: {}): {}", nhanVien.getId(), e.getMessage());
        }
    }

    public void sendKhachHangEmail(String toEmail, KhachHang khachHang, String rawPassword) {
        if (toEmail == null || toEmail.isBlank()) return;
        try {
            String subject = "[SevenStrike] Thông tin tài khoản khách hàng mới";
            String html = renderKhachHangTemplate(khachHang, rawPassword);

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(toEmail.trim());
            helper.setSubject(subject);
            helper.setText(html, true);

            addInlineLogo(helper);

            mailSender.send(message);
            log.info("Đã gửi mail welcome cho khách hàng: {}", khachHang.getTenTaiKhoan());

        } catch (Exception e) {
            log.error("Lỗi khi gửi mail khách hàng (ID: {}): {}", khachHang.getId(), e.getMessage());
        }
    }

    private void addInlineLogo(MimeMessageHelper helper) {
        Resource logo = new ClassPathResource(LOGO_PATH);
        if (!logo.exists()) {
            log.warn("Không tìm thấy logo inline: {} (hãy đặt file tại src/main/resources/{})", LOGO_PATH, LOGO_PATH);
            return;
        }
        try {
            helper.addInline("logo", logo);
        } catch (Exception e) {
            log.warn("Gắn logo inline thất bại", e);
        }
    }

    private String renderNhanVienTemplate(NhanVien nhanVien, String rawPassword) throws Exception {
        String template = readTemplate(TEMPLATE_NHAN_VIEN_PATH);

        String ten = safe(nhanVien.getTenNhanVien());
        String username = nhanVien.getTenTaiKhoan() != null ? nhanVien.getTenTaiKhoan() : "";
        String password = rawPassword != null ? rawPassword : "Vui lòng liên hệ quản lý";

        return template
                .replace("{{TEN}}", ten)
                .replace("{{TEN_TAI_KHOAN}}", username)
                .replace("{{MAT_KHAU}}", password)
                .replace("{{LINK_DANG_NHAP}}", safe(linkDangNhap))
                .replace("{{CURRENT_YEAR}}", String.valueOf(Year.now().getValue()));
    }

    private String renderKhachHangTemplate(KhachHang khachHang, String rawPassword) throws Exception {
        String template = readTemplate(TEMPLATE_KHACH_HANG_PATH);

        String ten = safe(khachHang.getTenKhachHang());
        String username = khachHang.getTenTaiKhoan() != null ? khachHang.getTenTaiKhoan() : "";
        String password = rawPassword != null ? rawPassword : "Vui lòng dùng chức năng quên mật khẩu";

        return template
                .replace("{{TEN}}", ten)
                .replace("{{TEN_TAI_KHOAN}}", username)
                .replace("{{MAT_KHAU}}", password)
                .replace("{{LINK_DANG_NHAP}}", safe(linkDangNhap))
                .replace("{{CURRENT_YEAR}}", String.valueOf(Year.now().getValue()));
    }

    private String readTemplate(String templatePath) throws Exception {
        ClassPathResource res = new ClassPathResource(templatePath);
        if (!res.exists()) {
            throw new IllegalStateException("Không tìm thấy template: " + templatePath);
        }
        try (var is = res.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
