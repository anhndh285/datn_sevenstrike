package com.example.datn_sevenstrike.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private static final String SYSTEM_PROMPT =
            "Bạn là trợ lý AI của cửa hàng giày bóng đá SevenStrike. " +
                    "Bạn hỗ trợ khách hàng về: sản phẩm giày bóng đá (size, màu sắc, thương hiệu, chất liệu, loại sân), " +
                    "chính sách đổi trả, vận chuyển, khuyến mãi và voucher giảm giá. " +
                    "Nếu câu hỏi liên quan đến khiếu nại, hoàn tiền, lỗi đơn hàng cụ thể, " +
                    "hoặc khách yêu cầu nói chuyện với nhân viên — chỉ trả về đúng chuỗi: CHUYEN_NHAN_VIEN. " +
                    "Trả lời ngắn gọn, thân thiện, bằng tiếng Việt. Không quá 200 từ.";

    private static final String SYSTEM_PROMPT_NOI_BO =
            "Bạn là trợ lý AI nội bộ của cửa hàng giày bóng đá SevenStrike, hỗ trợ nhân viên về: " +
                    "quy trình bán hàng, quản lý hóa đơn, tra cứu đơn hàng, lịch làm việc, " +
                    "chính sách nội bộ, quy định đổi ca, tồn kho sản phẩm. " +
                    "Nếu vấn đề cần sự phê duyệt của quản lý hoặc admin (ví dụ: hoàn tiền lớn, xử lý khiếu nại đặc biệt, " +
                    "thay đổi chính sách) — chỉ trả về đúng chuỗi: CHUYEN_NHAN_VIEN. " +
                    "Trả lời ngắn gọn, chuyên nghiệp, bằng tiếng Việt. Không quá 200 từ.";

    private final RestTemplate restTemplate = new RestTemplate();

    public String hoiGemini(String tinNhanKhach) {
        return goiGemini(
                SYSTEM_PROMPT,
                tinNhanKhach,
                "Xin lỗi, hiện tại tôi không thể xử lý yêu cầu của bạn. Vui lòng thử lại sau."
        );
    }

    public String hoiGeminiNoiBo(String tinNhan) {
        return goiGemini(
                SYSTEM_PROMPT_NOI_BO,
                tinNhan,
                "Xin lỗi, hiện tại tôi không thể xử lý yêu cầu. Vui lòng thử lại sau."
        );
    }

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
                log.info("[GeminiService] Gọi Gemini lần {} với model {}", attempt, "gemini-2.5-flash");

                ResponseEntity<Map> response = restTemplate.postForEntity(GEMINI_URL, entity, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    String text = extractText(response.getBody());
                    log.info("[GeminiService] Gemini phản hồi thành công");
                    return text;
                }

                log.warn("[GeminiService] Response không có body hợp lệ, status={}", response.getStatusCode());
            } catch (RestClientResponseException e) {
                int status = e.getStatusCode().value();
                String responseBody = e.getResponseBodyAsString();

                log.error("[GeminiService] Lỗi HTTP {} khi gọi Gemini. Body={}", status, responseBody);

                if (status == 429 && attempt < maxRetry) {
                    log.warn("[GeminiService] Rate limit 429, chờ 3 giây rồi thử lại lần {}", attempt + 1);
                    sleep3s();
                    continue;
                }

                if ((status == 500 || status == 503) && attempt < maxRetry) {
                    log.warn("[GeminiService] Gemini tạm lỗi {}, chờ 3 giây rồi thử lại lần {}", status, attempt + 1);
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
                    Object text = part.get("text");
                    if (text != null) {
                        return String.valueOf(text).trim();
                    }
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