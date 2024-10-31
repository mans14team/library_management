package com.example.library_management.global.config;

import com.example.library_management.domain.common.exception.GlobalExceptionConst;
import com.example.library_management.domain.membership.exception.PaymentException;
import com.example.library_management.domain.membership.exception.PaymentServerException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Duration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RestTemplateConfig {
    private final TossPaymentProperties tossPaymentProperties;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))  // 연결 타임아웃 5초
                .setReadTimeout(Duration.ofSeconds(5))     // 읽기 타임아웃 5초
                .additionalInterceptors(clientHttpRequestInterceptor())  // 로깅 인터셉터 추가
                .errorHandler(new CustomResponseErrorHandler())  // 커스텀 에러 핸들러
                .basicAuthentication(tossPaymentProperties.getSecretKey(), "")  // 토스 인증
                .build();
    }

    // HTTP 요청/응답 로깅을 위한 인터셉터
    private ClientHttpRequestInterceptor clientHttpRequestInterceptor() {
        return (request, body, execution) -> {
            logRequest(request, body);
            ClientHttpResponse response = execution.execute(request, body);
            logResponse(response);
            return response;
        };
    }

    // HTTP 요청 로깅
    private void logRequest(HttpRequest request, byte[] body) {
        if (log.isDebugEnabled()) {
            log.debug("===========================request begin================================================");
            log.debug("URI         : {}", request.getURI());
            log.debug("Method      : {}", request.getMethod());
            log.debug("Headers     : {}", request.getHeaders());
            log.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));
            log.debug("==========================request end================================================");
        }
    }

    // HTTP 응답 로깅
    private void logResponse(ClientHttpResponse response) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("============================response begin==========================================");
            log.debug("Status code  : {}", response.getStatusCode());
            log.debug("Headers      : {}", response.getHeaders());
            log.debug("=======================response end=================================================");
        }
    }

    // 커스텀 에러 핸들러
    private static class CustomResponseErrorHandler implements ResponseErrorHandler {
        @Override
        public boolean hasError(ClientHttpResponse response) throws IOException {
            return response.getStatusCode().is4xxClientError()
                    || response.getStatusCode().is5xxServerError();
        }

        @Override
        public void handleError(ClientHttpResponse response) throws IOException {
            if (response.getStatusCode().is5xxServerError()) {
                throw new PaymentServerException();
            } else if (response.getStatusCode().is4xxClientError()) {
                throw new PaymentException(GlobalExceptionConst.INVALID_PAYMENT_REQUEST);
            }
        }
    }
}

