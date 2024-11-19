package com.example.library_management.domain.membership.dto.response;

import com.example.library_management.domain.membership.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResponse {
    private String paymentKey;
    private String orderId;
    private Long amount;
    private PaymentStatus status;
}
