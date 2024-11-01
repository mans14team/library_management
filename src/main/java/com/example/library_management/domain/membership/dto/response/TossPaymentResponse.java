package com.example.library_management.domain.membership.dto.response;

import lombok.Getter;

@Getter
public class TossPaymentResponse {
    private String paymentKey;
    private String orderId;
    private Long amount;
    private String status;
    private String billingKey;
}