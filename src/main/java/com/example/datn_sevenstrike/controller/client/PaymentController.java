package com.example.datn_sevenstrike.controller.client;

import com.example.datn_sevenstrike.config.VNPayConfig;
import com.example.datn_sevenstrike.service.client.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final VNPayService vnPayService;
    private final VNPayConfig vnPayConfig;

    @org.springframework.beans.factory.annotation.Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

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
        // vnp_Amount luôn nhân 100, chia lại để lấy giá trị thực (VND)
        long vnpAmount = Long.parseLong(request.getParameter("vnp_Amount"));
        long totalPrice = vnpAmount / 100;

        // Redirect to Frontend success page
        String redirectUrl = frontendUrl + "/client/success?status=" + (paymentStatus == 1 ? "success" : "failed") +
                "&orderInfo=" + java.net.URLEncoder.encode(orderInfo, "UTF-8") +
                "&totalPrice=" + totalPrice +
                "&transactionId=" + transactionId;

        response.sendRedirect(redirectUrl);
    }

}