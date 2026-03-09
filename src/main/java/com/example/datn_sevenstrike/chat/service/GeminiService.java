package com.example.datn_sevenstrike.chat.service;

import com.example.datn_sevenstrike.dto.client.ProductClientDTO;
import com.example.datn_sevenstrike.dto.client.VariantClientDTO;
import com.example.datn_sevenstrike.service.client.ClientOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final ClientOrderService clientOrderService;

    public GeminiService(@Lazy ClientOrderService clientOrderService) {
        this.clientOrderService = clientOrderService;
    }

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private static final String SYSTEM_PROMPT =
            "Bạn là trợ lý AI của cửa hàng giày bóng đá SevenStrike — chuyên cung cấp giày bóng đá chính hãng.\n" +
                    "Nhiệm vụ: hỗ trợ khách hàng tìm sản phẩm, tư vấn và giải đáp thắc mắc bằng tiếng Việt.\n\n" +
                    "KIẾN THỨC SẢN PHẨM:\n" +
                    "• Thương hiệu: Nike, Adidas, Puma, Mizuno, New Balance\n" +
                    "• Loại sân — FG: cỏ tự nhiên | AG: cỏ nhân tạo sợi dài | TF: cỏ nhân tạo sợi ngắn (sân mini) | IC/IN: futsal trong nhà\n" +
                    "• Size chuẩn EU 36–47; gợi ý chọn size theo cm bàn chân\n\n" +
                    "CHÍNH SÁCH CỬA HÀNG:\n" +
                    "• Đổi trả: trong 7 ngày kể từ ngày nhận, sản phẩm nguyên vẹn chưa sử dụng\n" +
                    "• Vận chuyển: miễn phí đơn từ 500.000đ, phí 40.000đ đơn nhỏ hơn, giao 2–5 ngày toàn quốc\n" +
                    "• Thanh toán: COD (tiền mặt khi nhận) hoặc VNPay\n" +
                    "• Voucher/Phiếu giảm giá: dùng tại trang thanh toán\n" +
                    "• Theo dõi đơn hàng: đăng nhập → 'Đơn hàng của tôi'\n\n" +
                    "TƯ VẤN SẢN PHẨM:\n" +
                    "• Nếu có DANH SÁCH SẢN PHẨM bên dưới, hãy dùng để gợi ý cụ thể (tên, giá, size có sẵn)\n" +
                    "• Gợi ý 2–4 sản phẩm phù hợp nhất, không liệt kê tất cả\n" +
                    "• Nếu không có dữ liệu phù hợp, hướng dẫn khách dùng bộ lọc trên trang web\n" +
                    "• KHÔNG bịa đặt tên sản phẩm hoặc giá nếu không có trong dữ liệu\n\n" +
                    "CÁCH TRẢ LỜI:\n" +
                    "• Ngắn gọn, thân thiện — tối đa 150 từ\n" +
                    "• Dùng emoji phù hợp (⚽ 👟 📦 ✅ 🔥)\n" +
                    "• KHÔNG dùng dấu * hoặc ** (không markdown bold/italic)\n" +
                    "• KHÔNG dùng gạch đầu dòng bằng dấu *, thay bằng • hoặc số thứ tự\n\n" +
                    "CHUYỂN NHÂN VIÊN — chỉ trả về đúng chuỗi 'CHUYEN_NHAN_VIEN' (không kèm chữ nào khác) khi:\n" +
                    "• Khách khiếu nại, yêu cầu hoàn tiền, báo hàng lỗi/giả\n" +
                    "• Khách muốn hủy đơn đã đặt\n" +
                    "• Vấn đề kỹ thuật phức tạp liên quan đến đơn hàng cụ thể\n" +
                    "• Khách yêu cầu gặp nhân viên hoặc quản lý";

    private static final String SYSTEM_PROMPT_NOI_BO =
            "Bạn là trợ lý AI nội bộ của cửa hàng giày bóng đá SevenStrike, hỗ trợ nhân viên xử lý công việc hàng ngày.\n\n" +
                    "KIẾN THỨC NỘI BỘ:\n" +
                    "• Quy trình bán hàng tại quầy: tạo hóa đơn → thêm sản phẩm → chọn voucher → thanh toán → in hóa đơn\n" +
                    "• Đơn online: trạng thái Chờ xác nhận → Chờ giao hàng → Đang giao → Hoàn thành\n" +
                    "• Quản lý hóa đơn: xem, sửa, hủy đơn trong module Hóa đơn\n" +
                    "• Phiếu giảm giá: tạo phiếu cá nhân, phiếu công khai, gửi mail cho khách\n" +
                    "• Sản phẩm: quản lý tồn kho, thêm/sửa biến thể (màu, size), cập nhật giá\n" +
                    "• Đợt giảm giá: tạo và gán sản phẩm vào đợt giảm giá theo thời gian\n" +
                    "• Khách hàng: tra cứu thông tin, lịch sử mua hàng, xếp hạng thành viên\n" +
                    "• Báo cáo: doanh thu, sản phẩm bán chạy, thống kê theo khoảng thời gian\n\n" +
                    "CÁCH TRẢ LỜI:\n" +
                    "• Ngắn gọn, chuyên nghiệp, đi thẳng vào hướng dẫn — tối đa 150 từ\n" +
                    "• Ưu tiên các bước cụ thể, dùng số thứ tự\n" +
                    "• KHÔNG dùng dấu * hoặc ** (không markdown)\n" +
                    "• Nếu không biết quy trình cụ thể, thành thật trả lời và đề xuất hỏi quản lý\n\n" +
                    "CHUYỂN ADMIN — chỉ trả về đúng chuỗi 'CHUYEN_NHAN_VIEN' (không kèm chữ nào khác) khi:\n" +
                    "• Vấn đề cần phê duyệt của quản lý (hoàn tiền lớn, hủy đơn đặc biệt)\n" +
                    "• Thay đổi chính sách, quyền hệ thống\n" +
                    "• Sự cố kỹ thuật nghiêm trọng\n" +
                    "• Nhân viên yêu cầu gặp trực tiếp admin";

    private static final Pattern PRODUCT_KEYWORDS = Pattern.compile(
            "giày|sản phẩm|tìm|có bán|giá|mua|nike|adidas|puma|mizuno|new balance|" +
                    "fg|ag|tf|ic\\b|futsal|size|cỡ|hàng|mới nhất|giảm giá|khuyến mãi|sale|" +
                    "bán chạy|phổ biến|rẻ nhất|đắt nhất|khoảng|dưới|trên|từ.*đến|" +
                    "màu|xanh|đỏ|trắng|đen|vàng|cam|tím|hồng|xám|nâu|bạc",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
    );

    private final RestTemplate restTemplate = new RestTemplate();

    // ── Inner class giữ kết quả context sản phẩm ──────────────────────────────
    private static class ProductContextResult {
        final String context;
        final List<ProductClientDTO> products;
        ProductContextResult(String context, List<ProductClientDTO> products) {
            this.context = context;
            this.products = products;
        }
    }

    public String hoiGemini(String tinNhanKhach) {
        ProductContextResult result = buildProductContextResult(tinNhanKhach);
        String fullPrompt = SYSTEM_PROMPT;
        if (!result.context.isEmpty()) {
            fullPrompt += "\n\nDANH SÁCH SẢN PHẨM THỰC TẾ TỪ HỆ THỐNG (dùng để trả lời khách):\n"
                    + result.context
                    + "\n\nDùng dữ liệu trên để gợi ý sản phẩm phù hợp. Chỉ đề cập sản phẩm có trong danh sách.";
        }
        String geminiResponse = goiGemini(fullPrompt, tinNhanKhach,
                "Xin lỗi, hiện tại tôi không thể xử lý yêu cầu của bạn. Vui lòng thử lại sau.");

        // Chỉ gắn link cho sản phẩm còn hàng (check tồn kho ở cấp variant)
        if (!result.products.isEmpty()) {
            StringBuilder links = new StringBuilder("\n\n🔗 Xem sản phẩm:");
            result.products.stream()
                    .filter(p -> p.isHangCoSan() && p.getVariants() != null
                            && p.getVariants().stream()
                            .anyMatch(v -> v.getSoLuong() != null && v.getSoLuong() > 0))
                    .limit(4)
                    .forEach(p -> links.append("\n[").append(p.getTenSanPham())
                            .append("](/client/products/").append(p.getId()).append(")"));
            if (links.length() > "\n\n🔗 Xem sản phẩm:".length()) {
                geminiResponse += links.toString();
            }
        }
        return geminiResponse;
    }

    public String hoiGeminiNoiBo(String tinNhan) {
        return goiGemini(SYSTEM_PROMPT_NOI_BO, tinNhan,
                "Xin lỗi, hiện tại tôi không thể xử lý yêu cầu. Vui lòng thử lại sau.");
    }

    // ── Xây dựng ngữ cảnh sản phẩm từ CSDL ───────────────────────────────────
    private ProductContextResult buildProductContextResult(String query) {
        String q = query.toLowerCase();
        if (!PRODUCT_KEYWORDS.matcher(q).find()) return new ProductContextResult("", List.of());

        try {
            List<ProductClientDTO> products;
            String colorKeyword = "";

            if (q.contains("mới nhất") || q.contains("mới về") || q.contains("hàng mới") || q.contains("mới ra")) {
                products = clientOrderService.getNewArrivalProducts();
            } else if (q.contains("bán chạy") || q.contains("phổ biến") || q.contains("hot")) {
                products = clientOrderService.getBestSellingProducts();
            } else {
                products = clientOrderService.getProducts();

                if (q.contains("giảm giá") || q.contains("khuyến mãi") || q.contains("sale") || q.contains("ưu đãi")) {
                    products = products.stream()
                            .filter(p -> p.getPhanTramGiam() != null && p.getPhanTramGiam() > 0)
                            .sorted(Comparator.comparingInt(ProductClientDTO::getPhanTramGiam).reversed())
                            .collect(Collectors.toList());
                }

                String[] brands = {"nike", "adidas", "puma", "mizuno", "new balance"};
                for (String brand : brands) {
                    if (q.contains(brand)) {
                        products = products.stream()
                                .filter(p -> p.getTenThuongHieu() != null &&
                                        p.getTenThuongHieu().toLowerCase().contains(brand))
                                .collect(Collectors.toList());
                        break;
                    }
                }

                if (q.contains(" fg") || q.contains("sân cỏ tự nhiên")) {
                    products = filterByLoaiSan(products, "fg");
                } else if (q.contains(" ag") || q.contains("cỏ nhân tạo dày")) {
                    products = filterByLoaiSan(products, "ag");
                } else if (q.contains(" tf") || q.contains("sân mini") || q.contains("cỏ nhân tạo sợi ngắn")) {
                    products = filterByLoaiSan(products, "tf");
                } else if (q.contains(" ic") || q.contains(" in ") || q.contains("futsal") || q.contains("trong nhà")) {
                    products = filterByLoaiSan(products, "ic");
                }

                colorKeyword = extractColorKeyword(q);
                BigDecimal minPrice = extractMinPrice(q);
                BigDecimal maxPrice = extractMaxPrice(q);

                if (!colorKeyword.isEmpty()) {
                    final String color = colorKeyword;
                    final BigDecimal min = minPrice;
                    final BigDecimal max = maxPrice;
                    products = products.stream()
                            .filter(p -> p.getVariants() != null && p.getVariants().stream()
                                    .anyMatch(v -> variantMatchesColorAndPrice(v, color, min, max)))
                            .collect(Collectors.toList());
                } else if (minPrice != null || maxPrice != null) {
                    final BigDecimal min = minPrice;
                    final BigDecimal max = maxPrice;
                    products = products.stream()
                            .filter(p -> p.getGiaThapNhat() != null)
                            .filter(p -> min == null || p.getGiaThapNhat().compareTo(min) >= 0)
                            .filter(p -> max == null || p.getGiaThapNhat().compareTo(max) <= 0)
                            .collect(Collectors.toList());
                }

                String nameKeyword = extractNameKeyword(q);
                if (!nameKeyword.isEmpty()) {
                    List<ProductClientDTO> byName = products.stream()
                            .filter(p -> p.getTenSanPham() != null &&
                                    p.getTenSanPham().toLowerCase().contains(nameKeyword))
                            .collect(Collectors.toList());
                    if (!byName.isEmpty()) products = byName;
                }
            }

            // Chỉ lấy sản phẩm còn hàng (isHangCoSan = có ít nhất 1 variant soLuong > 0)
            List<ProductClientDTO> available = products.stream()
                    .filter(ProductClientDTO::isHangCoSan)
                    .limit(8)
                    .collect(Collectors.toList());

            if (available.isEmpty()) {
                return new ProductContextResult("Không tìm thấy sản phẩm phù hợp với yêu cầu.", List.of());
            }

            final String colorFinal = colorKeyword;
            String context = available.stream()
                    .map(p -> formatProduct(p, colorFinal))
                    .collect(Collectors.joining("\n"));
            return new ProductContextResult(context, available);

        } catch (Exception e) {
            log.error("[GeminiService] Lỗi lấy dữ liệu sản phẩm", e);
            return new ProductContextResult("", List.of());
        }
    }

    private List<ProductClientDTO> filterByLoaiSan(List<ProductClientDTO> products, String loai) {
        return products.stream()
                .filter(p -> p.getVariants() != null && p.getVariants().stream()
                        .anyMatch(v -> v.getTenLoaiSan() != null &&
                                v.getTenLoaiSan().toLowerCase().contains(loai)))
                .collect(Collectors.toList());
    }

    private String formatProduct(ProductClientDTO p, String colorFilter) {
        StringBuilder sb = new StringBuilder();
        sb.append("• ").append(p.getTenSanPham());
        if (p.getTenThuongHieu() != null) sb.append(" (").append(p.getTenThuongHieu()).append(")");

        if (!colorFilter.isEmpty() && p.getVariants() != null && !p.getVariants().isEmpty()) {
            List<VariantClientDTO> matching = p.getVariants().stream()
                    .filter(v -> v.getTenMauSac() != null
                            && v.getTenMauSac().toLowerCase().contains(colorFilter)
                            && v.getSoLuong() != null && v.getSoLuong() > 0)
                    .limit(4)
                    .collect(Collectors.toList());
            for (VariantClientDTO v : matching) {
                sb.append("\n  ↳ Màu ").append(v.getTenMauSac());
                if (v.getTenKichThuoc() != null) sb.append(", Size ").append(v.getTenKichThuoc());
                BigDecimal price = (v.getGiaSauGiam() != null && v.getPhanTramGiam() != null && v.getPhanTramGiam() > 0)
                        ? v.getGiaSauGiam() : v.getGiaBan();
                if (price != null) sb.append(" — ").append(formatPrice(price)).append("đ");
                if (v.getPhanTramGiam() != null && v.getPhanTramGiam() > 0)
                    sb.append(" (-").append(v.getPhanTramGiam()).append("%)");
            }
        } else {
            boolean hasDiscount = p.getPhanTramGiam() != null && p.getPhanTramGiam() > 0;
            if (hasDiscount && p.getGiaSauGiamThapNhat() != null) {
                sb.append(" — Giảm ").append(p.getPhanTramGiam()).append("%: ")
                        .append(formatPrice(p.getGiaSauGiamThapNhat())).append("đ");
                if (p.getGiaGocThapNhat() != null)
                    sb.append(" (gốc ").append(formatPrice(p.getGiaGocThapNhat())).append("đ)");
            } else if (p.getGiaThapNhat() != null) {
                sb.append(" — Giá: ").append(formatPrice(p.getGiaThapNhat())).append("đ");
                if (p.getGiaCaoNhat() != null && p.getGiaCaoNhat().compareTo(p.getGiaThapNhat()) > 0)
                    sb.append("–").append(formatPrice(p.getGiaCaoNhat())).append("đ");
            }
            if (p.getKichThuocCoSan() != null && !p.getKichThuocCoSan().isEmpty())
                sb.append(" | Size: ").append(String.join(", ", p.getKichThuocCoSan()));
        }
        return sb.toString();
    }

    private boolean variantMatchesColorAndPrice(VariantClientDTO v, String color,
                                                BigDecimal min, BigDecimal max) {
        if (v.getTenMauSac() == null) return false;
        if (!v.getTenMauSac().toLowerCase().contains(color)) return false;
        if (v.getSoLuong() == null || v.getSoLuong() <= 0) return false;
        BigDecimal price = (v.getGiaSauGiam() != null && v.getPhanTramGiam() != null && v.getPhanTramGiam() > 0)
                ? v.getGiaSauGiam() : v.getGiaBan();
        if (price == null) return true;
        if (min != null && price.compareTo(min) < 0) return false;
        if (max != null && price.compareTo(max) > 0) return false;
        return true;
    }

    private String extractColorKeyword(String query) {
        String[] colors = {
                "xanh lá", "xanh dương", "xanh navy", "xanh đen", "xanh ngọc", "xanh",
                "đỏ", "trắng", "đen", "vàng", "cam", "tím", "hồng", "xám", "nâu", "bạc"
        };
        for (String color : colors) {
            if (query.contains(color)) return color;
        }
        return "";
    }

    private String formatPrice(BigDecimal price) {
        return String.format("%,.0f", price);
    }

    private BigDecimal extractMinPrice(String query) {
        Pattern p = Pattern.compile("(?:từ|trên|hơn)\\s*(\\d+[,.]?\\d*)\\s*(triệu|tr\\b|nghìn|ngàn|k\\b)?");
        Matcher m = p.matcher(query);
        if (m.find()) return parsePrice(m.group(1), m.group(2));
        return null;
    }

    private BigDecimal extractMaxPrice(String query) {
        Pattern p = Pattern.compile("(?:đến|dưới|tầm|khoảng|tối đa)\\s*(\\d+[,.]?\\d*)\\s*(triệu|tr\\b|nghìn|ngàn|k\\b)?");
        Matcher m = p.matcher(query);
        if (m.find()) return parsePrice(m.group(1), m.group(2));
        return null;
    }

    private BigDecimal parsePrice(String number, String unit) {
        double val = Double.parseDouble(number.replace(",", "."));
        if (unit == null || unit.isEmpty()) {
            if (val <= 100) val *= 1_000_000;
        } else if (unit.startsWith("triệu") || unit.equals("tr")) {
            val *= 1_000_000;
        } else if (unit.startsWith("nghìn") || unit.startsWith("ngàn") || unit.equals("k")) {
            val *= 1_000;
        }
        return BigDecimal.valueOf(val);
    }

    private String extractNameKeyword(String query) {
        String cleaned = query
                .replaceAll("(?i)tìm|giày|sản phẩm|có bán|cho tôi|bán|loại|mẫu|nào|không|gì|cái|chiếc|đôi|ơi|bạn|ạ|nhé|mình|tôi|muốn|cần", "")
                .replaceAll("\\s+", " ").trim();
        return cleaned.length() >= 3 ? cleaned : "";
    }

    // ── Gọi Gemini API với retry ───────────────────────────────────────────────
    private String goiGemini(String systemPrompt, String tinNhan, String fallback) {
        Map<String, Object> systemInstruction = new LinkedHashMap<>();
        systemInstruction.put("parts", List.of(Map.of("text", systemPrompt)));

        Map<String, Object> userContent = Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", tinNhan == null ? "" : tinNhan))
        );

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("system_instruction", systemInstruction);
        requestBody.put("contents", List.of(userContent));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        int maxRetry = 3;
        for (int attempt = 1; attempt <= maxRetry; attempt++) {
            try {
                log.info("[GeminiService] Gọi Gemini lần {}", attempt);
                ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_URL, entity, Map.class);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    log.info("[GeminiService] Gemini phản hồi thành công");
                    return extractText(response.getBody());
                }
                log.warn("[GeminiService] Response không có body hợp lệ, status={}", response.getStatusCode());
            } catch (RestClientResponseException e) {
                int status = e.getStatusCode().value();
                log.error("[GeminiService] Lỗi HTTP {} khi gọi Gemini. Body={}", status, e.getResponseBodyAsString());
                if ((status == 429 || status == 500 || status == 503) && attempt < maxRetry) {
                    log.warn("[GeminiService] Chờ 3s rồi thử lại lần {}", attempt + 1);
                    sleep3s();
                    continue;
                }
                break;
            } catch (Exception e) {
                log.error("[GeminiService] Lỗi gọi Gemini API", e);
                break;
            }
        }
        return fallback;
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<?, ?> body) {
        try {
            List<?> candidates = (List<?>) body.get("candidates");
            if (candidates != null && !candidates.isEmpty()) {
                Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                Map<?, ?> content = (Map<?, ?>) candidate.get("content");
                List<?> parts = (List<?>) content.get("parts");
                if (parts != null && !parts.isEmpty()) {
                    Map<?, ?> part = (Map<?, ?>) parts.get(0);
                    String text = String.valueOf(part.get("text")).trim();
                    // Loại bỏ markdown bold/italic/bullet dùng dấu *
                    text = text.replaceAll("\\*\\*(.+?)\\*\\*", "$1")
                            .replaceAll("\\*(.+?)\\*", "$1")
                            .replaceAll("(?m)^\\*\\s+", "• ");
                    return text;
                }
            }
        } catch (Exception e) {
            log.error("[GeminiService] Lỗi parse response", e);
        }
        return "Xin lỗi, tôi không hiểu câu hỏi của bạn. Bạn có thể nói rõ hơn không?";
    }

    private void sleep3s() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}