package com.example.datn_sevenstrike.service.client;

import com.example.datn_sevenstrike.config.ZalopayConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ZalopayService {

    private final ZalopayConfig zalopayConfig;
    private final RestTemplate restTemplate;

    /**
     * Creates a Zalopay payment URL.
     *
     * @param amount  total in VND
     * @param orderId internal HoaDon id
     * @return the order_url from Zalopay's response
     */
    public String createPaymentUrl(long amount, Integer orderId) throws Exception {
        String appId      = zalopayConfig.getAppId();
        long   appTime    = System.currentTimeMillis();
        String appTransId = new SimpleDateFormat("yyMMdd").format(new Date())
                + "_" + orderId + "_" + appTime;
        String appUser    = "SevenStrike";
        String description = "SevenStrike - Thanh toan don hang #" + orderId;

        // embedData must contain redirecturl so Zalopay redirects after payment
        String embedData  = "{\"redirecturl\":\"" + zalopayConfig.getReturnUrl() + "\"}";
        String item       = "[]";

        // MAC = HMAC-SHA256(key1, app_id|app_trans_id|app_user|amount|app_time|embed_data|item)
        String macInput = appId + "|" + appTransId + "|" + appUser + "|"
                + amount + "|" + appTime + "|" + embedData + "|" + item;
        String mac = hmacSHA256(zalopayConfig.getKey1(), macInput);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("app_id",       Integer.parseInt(appId));
        body.put("app_trans_id", appTransId);
        body.put("app_user",     appUser);
        body.put("app_time",     appTime);
        body.put("amount",       amount);
        body.put("item",         item);
        body.put("description",  description);
        body.put("embed_data",   embedData);
        body.put("bank_code",    "");
        body.put("mac",          mac);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                zalopayConfig.getEndpoint(), HttpMethod.POST, entity, Map.class);

        Map<String, Object> result = response.getBody();
        if (result == null) throw new RuntimeException("Zalopay tra ve null");

        int returnCode = ((Number) result.getOrDefault("return_code", -1)).intValue();
        if (returnCode != 1) {
            throw new RuntimeException("Zalopay loi: " + result.get("return_message"));
        }

        return (String) result.get("order_url");
    }

    /**
     * Verifies the return callback from Zalopay.
     * Zalopay GET return params: status, apptransid, pmcid, bankcode, amount, discountamount, checksum
     */
    public boolean verifyReturnChecksum(Map<String, String> params) {
        try {
            // checksum = HMAC-SHA256(key2, appid|apptransid|pmcid|bankcode|amount|discountamount|status)
            String raw = zalopayConfig.getAppId()
                    + "|" + params.getOrDefault("apptransid",      "")
                    + "|" + params.getOrDefault("pmcid",           "")
                    + "|" + params.getOrDefault("bankcode",         "")
                    + "|" + params.getOrDefault("amount",           "")
                    + "|" + params.getOrDefault("discountamount",   "")
                    + "|" + params.getOrDefault("status",           "");
            String computed = hmacSHA256(zalopayConfig.getKey2(), raw);
            return computed.equalsIgnoreCase(params.getOrDefault("checksum", ""))
                    && "1".equals(params.getOrDefault("status", ""));
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