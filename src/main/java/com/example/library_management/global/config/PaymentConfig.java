package com.example.library_management.global.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Getter
@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
public class PaymentConfig {
    // 결제 관련 공통 설정
    @Value("${payment.retry.max-attempts}")
    private int maxRetryAttempts;

    @Value("${payment.retry.delay}")
    private long retryDelay;

    // retry 관련 Bean
    @Bean
    public RetryTemplate paymentRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxRetryAttempts);
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(retryDelay);

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);

        return retryTemplate;
    }
}
