// File: src/main/java/com/example/datn_sevenstrike/service/client/EmailService.java
package com.example.datn_sevenstrike.service.client;

import com.example.datn_sevenstrike.entity.HoaDon;
import com.example.datn_sevenstrike.entity.HoaDonChiTiet;
import com.example.datn_sevenstrike.repository.HoaDonChiTietRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("clientEmailService") // ✅ đổi tên bean để không đụng statistics EmailService
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final HoaDonChiTietRepository hoaDonChiTietRepo;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    // Màu chủ đạo theo logo SevenStrike
    private static final String RED = "#CC0000";
    private static final String DARK = "#1a1a1a";
    private static final String LIGHT_RED_BG = "#fff5f5";

    @Async
    @Transactional(readOnly = true)
    public void sendOrderConfirmation(HoaDon hoaDon) {
        if (hoaDon.getEmailKhachHang() == null || hoaDon.getEmailKhachHang().isBlank()) {
            return;
        }

        List<HoaDonChiTiet> details = hoaDonChiTietRepo.findAllWithProductByIdHoaDon(hoaDon.getId());
        String maHD = hoaDon.getMaHoaDon() != null ? hoaDon.getMaHoaDon() : String.valueOf(hoaDon.getId());
        String subject = "[SevenStrike] Xác nhận đơn hàng #" + maHD;
        String htmlBody = buildHtmlInvoice(hoaDon, details);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(hoaDon.getEmailKhachHang());
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            // Nhúng logo trực tiếp vào email bằng CID
            ClassPathResource logoResource = new ClassPathResource("static/logo.png");
            if (logoResource.exists()) {
                helper.addInline("logo", logoResource, "image/png");
            }

            mailSender.send(message);
            System.out.println("Da gui email xac nhan don hang #" + hoaDon.getId() + " toi " + hoaDon.getEmailKhachHang());
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Loi gui mail: " + e.getMessage());
        }
    }

    private String buildHtmlInvoice(HoaDon hd, List<HoaDonChiTiet> details) {
        NumberFormat vnFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String maHD = hd.getMaHoaDon() != null ? hd.getMaHoaDon() : String.valueOf(hd.getId());
        String trackingLink;
        try {
            trackingLink = String.format("%s/client/tracking?maHoaDon=%s&email=%s",
                    frontendUrl,
                    java.net.URLEncoder.encode(maHD, java.nio.charset.StandardCharsets.UTF_8.name()),
                    java.net.URLEncoder.encode(hd.getEmailKhachHang(), java.nio.charset.StandardCharsets.UTF_8.name()));
        } catch (Exception e) {
            trackingLink = frontendUrl + "/client/tracking";
        }

        int totalItems = details.stream().mapToInt(HoaDonChiTiet::getSoLuong).sum();

        BigDecimal tamTinh = hd.getTongTien() != null ? hd.getTongTien() : BigDecimal.ZERO;
        BigDecimal phiVanChuyen = hd.getPhiVanChuyen() != null ? hd.getPhiVanChuyen() : BigDecimal.ZERO;
        BigDecimal tongSauGiam = hd.getTongTienSauGiam() != null ? hd.getTongTienSauGiam() : BigDecimal.ZERO;
        BigDecimal giamGia = tamTinh.subtract(tongSauGiam);
        if (giamGia.compareTo(BigDecimal.ZERO) < 0) giamGia = BigDecimal.ZERO;
        BigDecimal tongCong = tongSauGiam.add(phiVanChuyen);

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'></head>");
        sb.append("<body style='margin:0;padding:0;background-color:#f0f0f0;font-family:Arial,Helvetica,sans-serif;'>");
        sb.append("<table width='100%' cellpadding='0' cellspacing='0' style='background-color:#f0f0f0;padding:30px 0;'><tr><td align='center'>");
        sb.append("<table width='600' cellpadding='0' cellspacing='0' style='background-color:#ffffff;border-radius:8px;overflow:hidden;'>");

        // ===== HEADER: Logo trên nền đỏ với viền tròn trắng =====
        sb.append("<tr><td style='background-color:").append(RED).append(";padding:24px;text-align:center;'>");
        sb.append("<div style='display:inline-block;background-color:#ffffff;border-radius:12px;padding:10px 20px;'>");
        sb.append("<img src='cid:logo' alt='SevenStrike' style='max-height:55px;display:block;' />");
        sb.append("</div>");
        sb.append("<div style='color:#ffffff;font-size:20px;font-weight:bold;letter-spacing:4px;margin-top:10px;'>SEVENSTRIKE</div>");
        sb.append("</td></tr>");

        // ===== Dải đen nhỏ phía dưới header =====
        sb.append("<tr><td style='background-color:").append(DARK).append(";height:6px;'></td></tr>");

        // ===== TIÊU ĐỀ =====
        sb.append("<tr><td style='padding:30px 30px 10px;text-align:center;'>");
        sb.append("<h1 style='margin:0;color:").append(DARK).append(";font-size:22px;'>Cảm ơn bạn đã đặt hàng</h1>");
        sb.append("</td></tr>");

        // ===== PHỤ ĐỀ =====
        sb.append("<tr><td style='padding:5px 30px 20px;text-align:center;color:#666;font-size:14px;'>");
        sb.append("Mail xác nhận đã được gửi tới <strong style='color:").append(RED).append(";'>").append(hd.getEmailKhachHang()).append("</strong>.");
        sb.append(" Hãy vui lòng kiểm tra lại hộp thư của bạn.");
        sb.append("</td></tr>");

        // ===== THANH TRẠNG THÁI (nền hồng nhạt, viền đỏ) =====
        sb.append("<tr><td style='padding:0 30px 25px;'>");
        sb.append("<table width='100%' cellpadding='0' cellspacing='0' style='background-color:").append(LIGHT_RED_BG).append(";border:1px solid #ffcccc;border-radius:8px;'>");
        sb.append("<tr>");
        // Cột 1: Checkmark
        sb.append("<td style='padding:15px;text-align:center;border-right:1px solid #ffcccc;width:33%;'>");
        sb.append("<div style='color:").append(RED).append(";font-size:22px;font-weight:bold;'>&#10003;</div>");
        sb.append("<div style='color:").append(RED).append(";font-size:12px;font-weight:bold;margin-top:4px;'>Đặt hàng thành công</div>");
        sb.append("</td>");
        // Cột 2: Mã đơn
        sb.append("<td style='padding:15px;text-align:center;border-right:1px solid #ffcccc;width:33%;'>");
        sb.append("<div style='color:#666;font-size:12px;'>Mã đơn</div>");
        sb.append("<div style='color:").append(RED).append(";font-weight:bold;font-size:15px;margin-top:2px;'>#").append(maHD).append("</div>");
        sb.append("</td>");
        // Cột 3: Số sản phẩm
        sb.append("<td style='padding:15px;text-align:center;width:33%;'>");
        sb.append("<div style='color:#666;font-size:12px;'>Sản phẩm</div>");
        sb.append("<div style='color:").append(DARK).append(";font-weight:bold;font-size:15px;margin-top:2px;'>").append(totalItems).append(" sản phẩm</div>");
        sb.append("</td>");
        sb.append("</tr></table>");
        sb.append("</td></tr>");

        // ===== THÔNG TIN MUA HÀNG =====
        sb.append("<tr><td style='padding:0 30px 20px;'>");
        sb.append("<table width='100%' cellpadding='0' cellspacing='0'>");
        sb.append("<tr><td colspan='2' style='padding-bottom:10px;font-size:15px;font-weight:bold;color:").append(DARK).append(";border-bottom:2px solid ").append(RED).append(";'>Thông tin mua hàng</td></tr>");
        sb.append("<tr><td style='padding:10px 0 6px;color:#888;font-size:13px;width:120px;'>Họ tên:</td><td style='padding:10px 0 6px;font-size:13px;color:#333;font-weight:bold;'>").append(hd.getTenKhachHang()).append("</td></tr>");
        sb.append("<tr><td style='padding:6px 0;color:#888;font-size:13px;'>Email:</td><td style='padding:6px 0;font-size:13px;color:#333;'>").append(hd.getEmailKhachHang()).append("</td></tr>");
        sb.append("<tr><td style='padding:6px 0;color:#888;font-size:13px;'>Điện thoại:</td><td style='padding:6px 0;font-size:13px;color:#333;'>").append(hd.getSoDienThoaiKhachHang()).append("</td></tr>");
        sb.append("</table>");
        sb.append("</td></tr>");

        // ===== ĐỊA CHỈ NHẬN HÀNG =====
        sb.append("<tr><td style='padding:0 30px 20px;'>");
        sb.append("<table width='100%' cellpadding='0' cellspacing='0'>");
        sb.append("<tr><td style='padding-bottom:10px;font-size:15px;font-weight:bold;color:").append(DARK).append(";border-bottom:2px solid ").append(RED).append(";'>Địa chỉ nhận hàng</td></tr>");
        sb.append("<tr><td style='padding:10px 0;font-size:13px;color:#333;'>").append(hd.getDiaChiKhachHang()).append("</td></tr>");
        sb.append("</table>");
        sb.append("</td></tr>");

        // ===== BẢNG CHI TIẾT ĐƠN HÀNG =====
        sb.append("<tr><td style='padding:0 30px 10px;'>");
        sb.append("<table width='100%' cellpadding='0' cellspacing='0'>");
        sb.append("<tr><td colspan='4' style='padding-bottom:10px;font-size:15px;font-weight:bold;color:").append(DARK).append(";border-bottom:2px solid ").append(RED).append(";'>Chi tiết đơn hàng</td></tr>");
        // Tiêu đề bảng
        sb.append("<tr style='background-color:#f9f9f9;'>");
        sb.append("<th style='padding:10px 8px;text-align:left;font-size:13px;color:#666;border-bottom:1px solid #eee;'>Sản phẩm</th>");
        sb.append("<th style='padding:10px 8px;text-align:right;font-size:13px;color:#666;border-bottom:1px solid #eee;'>Đơn giá</th>");
        sb.append("<th style='padding:10px 8px;text-align:center;font-size:13px;color:#666;border-bottom:1px solid #eee;'>SL</th>");
        sb.append("<th style='padding:10px 8px;text-align:right;font-size:13px;color:#666;border-bottom:1px solid #eee;'>Thành tiền</th>");
        sb.append("</tr>");

        for (HoaDonChiTiet item : details) {
            String productName;
            try {
                productName = item.getChiTietSanPham().getSanPham().getTenSanPham();
            } catch (Exception e) {
                productName = "Sản phẩm #" + item.getIdChiTietSanPham();
            }
            BigDecimal lineTotal = item.getDonGia().multiply(new BigDecimal(item.getSoLuong()));

            sb.append("<tr>");
            sb.append("<td style='padding:10px 8px;font-size:13px;color:#333;border-bottom:1px solid #f3f3f3;'>").append(productName).append("</td>");
            sb.append("<td style='padding:10px 8px;text-align:right;font-size:13px;color:#333;border-bottom:1px solid #f3f3f3;'>").append(vnFormat.format(item.getDonGia())).append("</td>");
            sb.append("<td style='padding:10px 8px;text-align:center;font-size:13px;color:#333;border-bottom:1px solid #f3f3f3;'>").append(item.getSoLuong()).append("</td>");
            sb.append("<td style='padding:10px 8px;text-align:right;font-size:13px;color:#333;border-bottom:1px solid #f3f3f3;'>").append(vnFormat.format(lineTotal)).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        sb.append("</td></tr>");

        // ===== TỔNG KẾT ĐƠN HÀNG =====
        sb.append("<tr><td style='padding:10px 30px 20px;'>");
        sb.append("<table width='100%' cellpadding='0' cellspacing='0' style='border-top:2px solid ").append(RED).append(";padding-top:15px;'>");

        sb.append("<tr><td colspan='2' style='padding-bottom:12px;font-size:14px;font-weight:bold;color:").append(DARK).append(";'>Đơn hàng: <span style='color:").append(RED).append(";'>#").append(maHD).append("</span></td></tr>");

        sb.append("<tr><td style='padding:4px 0;font-size:13px;color:#666;'>Tạm tính:</td>");
        sb.append("<td style='padding:4px 0;font-size:13px;color:#333;text-align:right;'>").append(vnFormat.format(tamTinh)).append("</td></tr>");

        sb.append("<tr><td style='padding:4px 0;font-size:13px;color:#666;'>Phí vận chuyển:</td>");
        sb.append("<td style='padding:4px 0;font-size:13px;color:#333;text-align:right;'>").append(vnFormat.format(phiVanChuyen)).append("</td></tr>");

        if (giamGia.compareTo(BigDecimal.ZERO) > 0) {
            sb.append("<tr><td style='padding:4px 0;font-size:13px;color:").append(RED).append(";font-weight:bold;'>Giảm giá:</td>");
            sb.append("<td style='padding:4px 0;font-size:13px;color:").append(RED).append(";font-weight:bold;text-align:right;'>-").append(vnFormat.format(giamGia)).append("</td></tr>");
        }

        sb.append("<tr><td colspan='2' style='padding-top:12px;border-top:1px solid #ddd;'></td></tr>");
        sb.append("<tr><td style='font-size:18px;font-weight:bold;color:").append(DARK).append(";'>Tổng cộng:</td>");
        sb.append("<td style='font-size:18px;font-weight:bold;color:").append(RED).append(";text-align:right;'>").append(vnFormat.format(tongCong)).append("</td></tr>");
        sb.append("</table>");
        sb.append("</td></tr>");

        // ===== GHI CHÚ + NÚT THEO DÕI =====
        sb.append("<tr><td style='padding:15px 30px 10px;font-size:13px;color:#666;'>");
        sb.append("Bạn có thể theo dõi trạng thái đơn hàng bằng cách nhấn nút <strong>\"Theo dõi đơn hàng\"</strong> bên dưới.");
        sb.append("</td></tr>");

        sb.append("<tr><td style='padding:5px 30px 30px;text-align:center;'>");
        sb.append("<a href='").append(trackingLink).append("' style='display:inline-block;background-color:").append(RED).append(";color:#ffffff;padding:14px 35px;text-decoration:none;border-radius:6px;font-weight:bold;font-size:14px;letter-spacing:1px;'>Theo dõi đơn hàng</a>");
        sb.append("</td></tr>");

        // ===== FOOTER =====
        sb.append("<tr><td style='background-color:").append(DARK).append(";padding:20px 30px;text-align:center;'>");
        sb.append("<div style='font-size:12px;color:#aaa;'>Nếu bạn không thực hiện đơn hàng này, vui lòng liên hệ qua email</div>");
        sb.append("<div style='font-size:14px;font-weight:bold;color:#ffffff;margin-top:6px;letter-spacing:2px;'>SEVENSTRIKE</div>");
        sb.append("</td></tr>");

        sb.append("</table>");
        sb.append("</td></tr></table>");
        sb.append("</body></html>");

        return sb.toString();
    }
}