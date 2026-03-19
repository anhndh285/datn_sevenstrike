package com.example.datn_sevenstrike.service.client;

import com.example.datn_sevenstrike.config.VietQRConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VietQRService {

    private final VietQRConfig vietQRConfig;

    /**
     * Generates VietQR image URL and transfer details.
     * No HTTP call needed — VietQR.io provides a stateless image URL API.
     *
     * @param amount  VND amount
     * @param orderId internal HoaDon id
     * @return Map with keys: qrImageUrl, bankId, accountNo, accountName, amount, memo
     */
    public Map<String, Object> generateQrData(long amount, Integer orderId) {
        // Use ASCII-only memo to ensure compatibility with all bank QR readers
        String memo = "SevenStrike DH" + orderId;

        String encodedMemo = URLEncoder.encode(memo, StandardCharsets.UTF_8);
        String encodedName = URLEncoder.encode(vietQRConfig.getAccountName(), StandardCharsets.UTF_8);

        String qrImageUrl = String.format(
                "https://img.vietqr.io/image/%s-%s-%s.jpg?amount=%d&addInfo=%s&accountName=%s",
                vietQRConfig.getBankId(),
                vietQRConfig.getAccountNo(),
                vietQRConfig.getTemplate(),
                amount,
                encodedMemo,
                encodedName
        );

        Map<String, Object> data = new HashMap<>();
        data.put("qrImageUrl",  qrImageUrl);
        data.put("bankId",      vietQRConfig.getBankId());
        data.put("accountNo",   vietQRConfig.getAccountNo());
        data.put("accountName", vietQRConfig.getAccountName());
        data.put("amount",      amount);
        data.put("memo",        memo);
        return data;
    }
}