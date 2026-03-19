package com.example.datn_sevenstrike.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class VietQRConfig {

    @Value("${vietqr.bankId}")
    private String bankId;

    @Value("${vietqr.accountNo}")
    private String accountNo;

    @Value("${vietqr.accountName}")
    private String accountName;

    @Value("${vietqr.template:compact2}")
    private String template;
}