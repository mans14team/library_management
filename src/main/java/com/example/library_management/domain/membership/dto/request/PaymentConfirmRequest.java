package com.example.library_management.domain.membership.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@AllArgsConstructor
public class PaymentConfirmRequest {
    private String paymentKey;
    private String orderId;
    private Long amount;
}
