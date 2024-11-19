package com.example.library_management.domain.membership.dto.response;

import com.example.library_management.domain.membership.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Getter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@Builder
public class PaymentResponse {
    private String paymentKey;
    private String orderId;
    private Long amount;
    private PaymentStatus status;
}
