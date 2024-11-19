package com.example.library_management.domain.membership.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
public class TossPaymentResponse {
    private String paymentKey;
    private String orderId;
    private Long amount;
    private String status;
    private String billingKey;
}