package com.example.library_management.domain.membership.controller;

import com.example.library_management.domain.membership.dto.response.PaymentRequestResponse;
import com.example.library_management.domain.membership.dto.response.PaymentResponse;
import com.example.library_management.domain.membership.service.PaymentService;
import com.example.library_management.global.config.ApiResponse;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/library/payments")
public class PaymentController {
    private final PaymentService paymentService;


    // 1. 결제 요청
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<PaymentRequestResponse>> requestPayment(@AuthenticationPrincipal UserDetailsImpl userDetails){
        PaymentRequestResponse response = paymentService.createPaymentRequest(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 2. 결제 승인 요청 (클라이언트 결제 성공 후 호출)
    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(@RequestParam String paymentKey,
                                                                       @RequestParam String orderId,
                                                                       @RequestParam Long amount,
                                                                       @AuthenticationPrincipal UserDetailsImpl userDetails){
        PaymentResponse response = paymentService.confirmPayment(paymentKey, orderId, amount, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    // 3. 결제 실패 처리
    @GetMapping("/fail")
    public ResponseEntity<ApiResponse<String>> failPayment(@RequestParam String code, @RequestParam String message, @RequestParam String orderId){
        return ResponseEntity.ok(ApiResponse.success(paymentService.handlePaymentFailure(code, message, orderId)));
    }
}
