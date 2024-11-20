package com.example.library_management.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CustomConfig {

    // Redis 분산락을 동적으로 활성화 할 기간을 yml파일로 분리하여 상품화에 유동적으로 대응이 가능.
    @Value("${custom.active-months}")
    private List<Integer> activeMonths;

    public List<Integer> getActiveMonths() {
        return activeMonths;
    }
}
