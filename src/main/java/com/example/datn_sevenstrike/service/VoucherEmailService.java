package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.entity.PhieuGiamGia;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherEmailService {

    private final JavaMailSender mailSender;

    // ✅ File phải nằm tại: src/main/resources/templates/voucher_email_template.html
    private static final String TEMPLATE_PATH = "templates/voucher_email_template.html";

    // ✅ File phải nằm tại: src/main/resources/static/logo.png
    private static final String LOGO_PATH = "static/logo.png";

    private static final DateTimeFormatter FMT_NGAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale LOCALE_VI = new Locale("vi", "VN");

    public void sendVoucherEmail(String toEmail, PhieuGiamGia phieu) throws Exception {
        if (toEmail == null || toEmail.isBlank()) return;
        if (phieu == null) return;

        String subject = buildSubject(phieu);
        String html = renderTemplate(phieu);

        MimeMessage message = mailSender.createMimeMessage();

        // MULTIPART_MODE_MIXED_RELATED giúp inline image ổn định hơn
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
    }

    private void addInlineLogo(MimeMessageHelper helper) {
        Resource logo = new ClassPathResource(LOGO_PATH);
        if (!logo.exists()) {
            log.warn("Không tìm thấy logo inline: {} (hãy đặt file tại src/main/resources/{})", LOGO_PATH, LOGO_PATH);
            return;
        }
        try {
            // ✅ Tự suy luận content-type
            helper.addInline("logo", logo);
        } catch (Exception e) {
            log.warn("Gắn logo inline thất bại", e);
        }
    }

    private String buildSubject(PhieuGiamGia phieu) {
        String ma = safe(phieu.getMaPhieuGiamGia());
        String ten = safe(phieu.getTenPhieuGiamGia());
        if (!ten.isBlank()) {
            return "[SevenStrike] Phiếu giảm giá " + ma + " - " + ten;
        }
        return "[SevenStrike] Phiếu giảm giá " + ma;
    }

    private String renderTemplate(PhieuGiamGia phieu) throws Exception {
        String template = readTemplate();

        String ma = safe(phieu.getMaPhieuGiamGia());
        String giaTriGiam = buildGiaTriGiam(phieu.getLoaiPhieuGiamGia(), phieu.getGiaTriGiamGia());
        String hoaDonToiThieu = formatVnd(phieu.getHoaDonToiThieu());
        String ngayKetThuc = formatNgay(phieu.getNgayKetThuc());
        String year = String.valueOf(Year.now().getValue());

        return template
                .replace("{{MA_GIAM_GIA}}", ma)
                .replace("{{GIA_TRI_GIAM}}", giaTriGiam)
                .replace("{{HOA_DON_TOI_THIEU}}", hoaDonToiThieu)
                .replace("{{NGAY_KET_THUC}}", ngayKetThuc)
                .replace("{{CURRENT_YEAR}}", year);
    }

    private String readTemplate() throws Exception {
        ClassPathResource res = new ClassPathResource(TEMPLATE_PATH);
        if (!res.exists()) {
            // ❌ Không fallback nữa để bắt lỗi “đặt sai chỗ”
            throw new IllegalStateException(
                    "Không tìm thấy template email: " + TEMPLATE_PATH
                            + ". Hãy đặt file tại: src/main/resources/" + TEMPLATE_PATH
            );
        }
        try (var is = res.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String buildGiaTriGiam(Boolean loaiGiamTheoTien, BigDecimal giaTri) {
        BigDecimal v = (giaTri == null) ? BigDecimal.ZERO : giaTri;
        boolean giamTheoTien = Boolean.TRUE.equals(loaiGiamTheoTien);

        if (!giamTheoTien) {
            String num = v.stripTrailingZeros().toPlainString();
            return num + "%";
        }

        return formatVnd(v) + " VNĐ";
    }

    private String formatVnd(BigDecimal v) {
        BigDecimal x = (v == null) ? BigDecimal.ZERO : v;
        NumberFormat nf = NumberFormat.getInstance(LOCALE_VI);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        return nf.format(x);
    }

    private String formatNgay(LocalDate d) {
        if (d == null) return "";
        return d.format(FMT_NGAY);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
