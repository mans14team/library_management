package com.example.library_management.domain.membership.controller;

import com.example.library_management.domain.membership.dto.request.PaymentSearchCondition;
import com.example.library_management.domain.membership.dto.response.PaymentRequestResponse;
import com.example.library_management.domain.membership.dto.response.PaymentResponse;
import com.example.library_management.domain.membership.dto.response.PaymentSearchResult;
import com.example.library_management.domain.membership.enums.PaymentStatus;
import com.example.library_management.domain.membership.service.PaymentService;
import com.example.library_management.global.config.ApiResponse;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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

    // 결제 내역 조회 API
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<PaymentSearchResult>>> getPaymentHistory(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate startDate,
                                                                                    @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd")LocalDate endDate,
                                                                                    @RequestParam(required = false)PaymentStatus status,
                                                                                    @PageableDefault(size = 10, sort = "paidAt", direction = Sort.Direction.DESC)Pageable pageable){
        PaymentSearchCondition condition = PaymentSearchCondition.builder()
                .startDate(startDate)
                .endDate(endDate)
                .status(status)
                .build();

        Page<PaymentSearchResult> response = paymentService.getPaymentHistory(userDetails.getUser(), condition, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 결제 상세 내역 조회 API

    // 환불 처리
    @PostMapping("/refund/{paymentId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundPayment(@PathVariable Long paymentId, @RequestParam(required = false) String cancelReason, @AuthenticationPrincipal UserDetailsImpl userDetails){
        PaymentResponse response = paymentService.refundPayment(paymentId, cancelReason, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    // 자동 결제 기능
    @PostMapping("/auto-payment/register")
    public ResponseEntity<ApiResponse<String>> registerAutoPayment(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return ResponseEntity.ok(ApiResponse.success(paymentService.registerAutoPayment(userDetails.getUser())));
    }
}
