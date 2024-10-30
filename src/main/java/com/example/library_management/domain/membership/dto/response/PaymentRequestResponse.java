package com.example.library_management.domain.membership.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentRequestResponse {
    private Long amount;
    private String orderId;
    private String orderName;
    private String successUrl;
    private String failUrl;
}
