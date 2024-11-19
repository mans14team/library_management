package com.example.library_management.domain.membership.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Getter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@Builder
public class PaymentRequestResponse {
    private Long amount;
    private String orderId;
    private String orderName;
    private String successUrl;
    private String failUrl;
}
