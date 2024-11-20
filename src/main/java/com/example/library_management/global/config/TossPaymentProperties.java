package com.example.library_management.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "toss")   // yml의 "toss" 아래 프로퍼티들과 매핑
public class TossPaymentProperties {
    private String secretKey;
    private String clientKey;
    private String successUrl;
    private String failUrl;

    private Api api = new Api();

    @Getter
    @Setter
    public static class Api {
        private String baseUrl = "https://api.tosspayments.com/v1";
        private String confirmUrl = "/payments/confirm";   // 결제 승인 URL
        private String paymentUrl = "/payments";  // 결제 조회 URL
        private String cancelUrl = "/payments/{paymentKey}/cancel";  // 결제 취소 URL
        private String billingUrl = "/billing/authorizations/card";  // 빌링키 발급 URL (자동결제용)
    }
}
