package com.example.datn_sevenstrike.service.client;

import com.example.datn_sevenstrike.config.MomoConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MomoService {

    private final MomoConfig momoConfig;
    private final RestTemplate restTemplate;

    /**
     * Creates a Momo payment URL.
     *
     * @param amount  total in VND (no multiplier, unlike VNPay)
     * @param orderId internal HoaDon id
     * @return the payUrl from Momo's response
     */
    public String createPaymentUrl(long amount, Integer orderId) throws Exception {
        String partnerCode = momoConfig.getPartnerCode();
        String requestId   = partnerCode + System.currentTimeMillis();
        String momoOrderId = partnerCode + "_" + orderId + "_" + System.currentTimeMillis();
        String orderInfo   = "Thanh toan don hang " + orderId;
        String redirectUrl = momoConfig.getReturnUrl();
        String ipnUrl      = momoConfig.getNotifyUrl();
        String requestType = momoConfig.getRequestType();
        String extraData   = "";

        // Raw signature string: alphabetical key order (required by Momo v2)
        String rawSignature = "accessKey="    + momoConfig.getAccessKey()
                + "&amount="      + amount
                + "&extraData="   + extraData
                + "&ipnUrl="      + ipnUrl
                + "&orderId="     + momoOrderId
                + "&orderInfo="   + orderInfo
                + "&partnerCode=" + partnerCode
                + "&redirectUrl=" + redirectUrl
                + "&requestId="   + requestId
                + "&requestType=" + requestType;

        String signature = hmacSHA256(momoConfig.getSecretKey(), rawSignature);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("partnerCode", partnerCode);
        body.put("requestId",   requestId);
        body.put("amount",      amount);
        body.put("orderId",     momoOrderId);
        body.put("orderInfo",   orderInfo);
        body.put("redirectUrl", redirectUrl);
        body.put("ipnUrl",      ipnUrl);
        body.put("lang",        "vi");
        body.put("extraData",   extraData);
        body.put("requestType", requestType);
        body.put("signature",   signature);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                momoConfig.getEndpoint(), HttpMethod.POST, entity, Map.class);

        Map<String, Object> result = response.getBody();
        if (result == null) throw new RuntimeException("Momo tra ve null");

        int resultCode = ((Number) result.getOrDefault("resultCode", -1)).intValue();
        if (resultCode != 0) {
            throw new RuntimeException("Momo loi: " + result.get("message"));
        }

        return (String) result.get("payUrl");
    }

    /**
     * Verifies Momo's return/IPN callback signature.
     * Returns true if the signature matches and resultCode == 0.
     */
    public boolean verifyCallback(Map<String, String> params) {
        try {
            String rawHash = "accessKey="     + momoConfig.getAccessKey()
                    + "&amount="       + params.getOrDefault("amount", "")
                    + "&extraData="    + params.getOrDefault("extraData", "")
                    + "&message="      + params.getOrDefault("message", "")
                    + "&orderId="      + params.getOrDefault("orderId", "")
                    + "&orderInfo="    + params.getOrDefault("orderInfo", "")
                    + "&orderType="    + params.getOrDefault("orderType", "")
                    + "&partnerCode="  + params.getOrDefault("partnerCode", "")
                    + "&payType="      + params.getOrDefault("payType", "")
                    + "&requestId="    + params.getOrDefault("requestId", "")
                    + "&responseTime=" + params.getOrDefault("responseTime", "")
                    + "&resultCode="   + params.getOrDefault("resultCode", "")
                    + "&transId="      + params.getOrDefault("transId", "");

            String computed = hmacSHA256(momoConfig.getSecretKey(), rawHash);
            String received = params.getOrDefault("signature", "");

            return computed.equals(received) && "0".equals(params.getOrDefault("resultCode", ""));
        } catch (Exception e) {
            return false;
        }
    }

    public static String hmacSHA256(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : hash) hex.append(String.format("%02x", b & 0xff));
        return hex.toString();
    }
}