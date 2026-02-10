package com.example.datn_sevenstrike.service;

import com.example.datn_sevenstrike.entity.PhieuGiamGia;
import com.example.datn_sevenstrike.repository.PhieuGiamGiaRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoucherEmailService {

    private final JavaMailSender mailSender;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Async
    public void sendVoucherEmailAsync(List<String> emails, Integer voucherId) {
        try {
            PhieuGiamGia voucher = phieuGiamGiaRepository.findById(voucherId).orElse(null);
            if (voucher == null) {
                System.err.println("[MAIL] Không tìm thấy voucherId=" + voucherId);
                return;
            }

            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            boolean giamPhanTram =
                    voucher.getGiaTriGiamGia() != null && voucher.getGiaTriGiamGia().compareTo(BigDecimal.ZERO) > 0;

            String hienThiGiam = giamPhanTram
                    ? (voucher.getGiaTriGiamGia() == null ? "0"
                    : voucher.getGiaTriGiamGia().stripTrailingZeros().toPlainString()) + "%"
                    : formatter.format(voucher.getSoTienGiamToiDa() == null
                    ? BigDecimal.ZERO : voucher.getSoTienGiamToiDa()) + " VNĐ";

            // ✅ Load template UTF-8 chuẩn
            ClassPathResource htmlResource = new ClassPathResource("voucher_email_template.html");
            String htmlTemplate;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(htmlResource.getInputStream(), StandardCharsets.UTF_8))) {
                htmlTemplate = reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }

            String finalHtmlContent = htmlTemplate
                    .replace("{{MA_GIAM_GIA}}", String.valueOf(voucher.getMaPhieuGiamGia()))
                    .replace("{{GIA_TRI_GIAM}}", hienThiGiam)
                    .replace("{{HOA_DON_TOI_THIEU}}", formatter.format(voucher.getHoaDonToiThieu() == null
                            ? BigDecimal.ZERO : voucher.getHoaDonToiThieu()))
                    .replace("{{NGAY_KET_THUC}}", voucher.getNgayKetThuc() == null
                            ? "---" : voucher.getNgayKetThuc().format(dateFormatter))
                    .replace("{{CURRENT_YEAR}}", String.valueOf(Year.now().getValue()));

            for (String email : emails) {
                try {
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

                    // ✅ QUAN TRỌNG: setFrom
                    helper.setFrom("sevenstrike8@gmail.com", "SevenStrike");

                    helper.setTo(email);
                    helper.setSubject("Mã giảm giá đặc biệt từ SevenStrike");
                    helper.setText(finalHtmlContent, true);

                    mailSender.send(message);
                } catch (Exception one) {
                    System.err.println("[MAIL] Gửi fail tới: " + email);
                    one.printStackTrace();
                }
            }
        } catch (Exception ex) {
            System.err.println("[MAIL] Lỗi tổng khi gửi voucherId=" + voucherId);
            ex.printStackTrace();
        }
    }
}
