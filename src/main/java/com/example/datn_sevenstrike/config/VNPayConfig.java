package com.example.datn_sevenstrike.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
@Getter
public class VNPayConfig {

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String secretKey;

    @Value("${vnpay.payUrl}")
    private String vnp_PayUrl;

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;

    @Value("${vnpay.ipnUrl}")
    private String vnp_IpnUrl;

    @Value("${vnpay.version:2.1.0}")
    private String vnp_Version;

    @Value("${vnpay.command:pay}")
    private String vnp_Command;

    @Value("${vnpay.orderType:other}")
    private String vnp_OrderType;

    @Value("${vnpay.currCode:VND}")
    private String vnp_CurrCode;

    @Value("${vnpay.locale:vn}")
    private String vnp_Locale;

    @Value("${vnpay.expireMinutes:15}")
    private Integer expireMinutes;

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParams = new HashMap<>();

        vnpParams.put("vnp_Version", vnp_Version);
        vnpParams.put("vnp_Command", vnp_Command);
        vnpParams.put("vnp_TmnCode", vnp_TmnCode);
        vnpParams.put("vnp_Locale", vnp_Locale);
        vnpParams.put("vnp_CurrCode", vnp_CurrCode);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        String vnpCreateDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_CreateDate", vnpCreateDate);

        cld.add(Calendar.MINUTE, expireMinutes);
        String vnpExpireDate = formatter.format(cld.getTime());
        vnpParams.put("vnp_ExpireDate", vnpExpireDate);

        return vnpParams;
    }
}