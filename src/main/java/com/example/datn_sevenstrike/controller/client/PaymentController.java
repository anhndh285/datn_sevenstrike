package com.example.datn_sevenstrike.controller.client;

import com.example.datn_sevenstrike.config.VNPayConfig;
import com.example.datn_sevenstrike.service.client.ClientOrderService;
import com.example.datn_sevenstrike.service.client.MomoService;
import com.example.datn_sevenstrike.service.client.VNPayService;
import com.example.datn_sevenstrike.service.client.VietQRService;
import com.example.datn_sevenstrike.service.client.ZalopayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final VNPayService        vnPayService;
    private final VNPayConfig         vnPayConfig;
    private final MomoService         momoService;
    private final ZalopayService      zalopayService;
    private final VietQRService       vietQRService;
    private final ClientOrderService  clientOrderService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    // ======================================================
    // VNPay (existing)
    // ======================================================

    @PostMapping("/create_payment")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> req) {
        try {
            int amount = Integer.parseInt(req.get("amount").toString());
            String orderInfo = req.get("orderInfo") != null ? req.get("orderInfo").toString() : "Thanh toan don hang";
            String urlReturn = req.get("returnUrl") != null ? req.get("returnUrl").toString() : null;

            String paymentUrl = vnPayService.createOrder(amount, orderInfo, urlReturn);
            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating payment: " + e.getMessage());
        }
    }

    @GetMapping("/vnpay_return")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        if (orderInfo == null || orderInfo.isBlank()) orderInfo = "Khong co thong tin";
        String transactionId = request.getParameter("vnp_TransactionNo");
        long vnpAmount = Long.parseLong(request.getParameter("vnp_Amount"));
        long totalPrice = vnpAmount / 100;

        String redirectUrl = frontendUrl + "/client/success?status=" + (paymentStatus == 1 ? "success" : "failed")
                + "&orderInfo=" + URLEncoder.encode(orderInfo, StandardCharsets.UTF_8)
                + "&totalPrice=" + totalPrice
                + "&transactionId=" + transactionId;

        response.sendRedirect(redirectUrl);
    }

    // ======================================================
    // Momo (loaiThanhToan=2)
    // ======================================================

    @PostMapping("/momo/create")
    public ResponseEntity<?> createMomoPayment(@RequestBody Map<String, Object> req) {
        try {
            long amount     = Long.parseLong(req.get("amount").toString());
            Integer orderId = Integer.parseInt(req.get("orderId").toString());
            String paymentUrl = momoService.createPaymentUrl(amount, orderId);
            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating Momo payment: " + e.getMessage());
        }
    }

    @GetMapping("/momo_return")
    public void momoReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(k -> params.put(k, request.getParameter(k)));

        boolean success   = momoService.verifyCallback(params);
        String  orderInfo = params.getOrDefault("orderInfo", "Momo payment");
        long    amount    = 0;
        try { amount = Long.parseLong(params.getOrDefault("amount", "0")); } catch (Exception ignored) {}

        if (!success) {
            try {
                String[] parts = params.getOrDefault("orderId", "").split("_");
                if (parts.length >= 2) {
                    clientOrderService.cancelOrderOnPaymentFailure(Integer.parseInt(parts[1]));
                }
            } catch (Exception ignored) {}
        }

        String redirectUrl = frontendUrl + "/client/success?status=" + (success ? "success" : "failed")
                + "&orderInfo=" + URLEncoder.encode(orderInfo, StandardCharsets.UTF_8)
                + "&totalPrice=" + amount
                + "&gateway=momo";
        response.sendRedirect(redirectUrl);
    }

    // ======================================================
    // Zalopay (loaiThanhToan=3)
    // ======================================================

    @PostMapping("/zalopay/create")
    public ResponseEntity<?> createZalopayPayment(@RequestBody Map<String, Object> req) {
        try {
            long amount     = Long.parseLong(req.get("amount").toString());
            Integer orderId = Integer.parseInt(req.get("orderId").toString());
            String paymentUrl = zalopayService.createPaymentUrl(amount, orderId);
            Map<String, String> response = new HashMap<>();
            response.put("paymentUrl", paymentUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating Zalopay payment: " + e.getMessage());
        }
    }

    @GetMapping("/zalopay_return")
    public void zalopayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> params = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(k -> params.put(k, request.getParameter(k)));

        boolean success  = "1".equals(params.getOrDefault("status", ""));
        String  transId  = params.getOrDefault("apptransid", "");
        long    amount   = 0;
        try { amount = Long.parseLong(params.getOrDefault("amount", "0")); } catch (Exception ignored) {}

        if (!success) {
            try {
                String[] parts = transId.split("_");
                if (parts.length >= 2) {
                    clientOrderService.cancelOrderOnPaymentFailure(Integer.parseInt(parts[1]));
                }
            } catch (Exception ignored) {}
        }

        String redirectUrl = frontendUrl + "/client/success?status=" + (success ? "success" : "failed")
                + "&orderInfo=" + URLEncoder.encode("Zalopay: " + transId, StandardCharsets.UTF_8)
                + "&totalPrice=" + amount
                + "&gateway=zalopay";
        response.sendRedirect(redirectUrl);
    }

    // ======================================================
    // VietQR (loaiThanhToan=4)
    // ======================================================

    @PostMapping("/vietqr/generate")
    public ResponseEntity<?> generateVietQR(@RequestBody Map<String, Object> req) {
        try {
            long    amount  = Long.parseLong(req.get("amount").toString());
            Integer orderId = Integer.parseInt(req.get("orderId").toString());
            Map<String, Object> qrData = vietQRService.generateQrData(amount, orderId);
            return ResponseEntity.ok(qrData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating VietQR: " + e.getMessage());
        }
    }
}