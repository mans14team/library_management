package com.example.library_management.domain.test;

import com.example.library_management.global.config.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/library/test")
public class SecurityTestController {

    @GetMapping("/secured")
    public ResponseEntity<ApiResponse<String>> testSecuredEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("보호된 엔드포인트 접근 성공"));
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> testPublicEndpoint() {
        return ResponseEntity.ok(ApiResponse.success("공개 엔드포인트 접근 성공"));
    }
}