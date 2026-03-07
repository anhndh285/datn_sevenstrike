package com.example.datn_sevenstrike.ghn.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(GhnConfig.GhnProperties.class)
public class GhnConfig {

    @Bean
    public RestTemplate ghnRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(20_000);
        return new RestTemplate(factory);
    }

    @Data
    @ConfigurationProperties(prefix = "ghn")
    public static class GhnProperties {
        private String token;
        private Integer shopId;
        private String baseUrl = "https://dev-online-gateway.ghn.vn";

        // Optional override (không bắt buộc nếu auto lấy từ shop/all)
        private Integer fromDistrictId;
        private String fromWardCode;
    }
}