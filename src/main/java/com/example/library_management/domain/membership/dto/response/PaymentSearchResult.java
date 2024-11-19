package com.example.library_management.domain.membership.dto.response;

import com.example.library_management.domain.membership.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
public class PaymentSearchResult {
    private Long paymentId;
    private String paymentKey;
    private String orderId;
    private Long amount;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private String membershipStatus;
    private LocalDateTime membershipEndDate;
    private String userName;

    @QueryProjection
    public PaymentSearchResult(Long paymentId, String paymentKey, String orderId,
                               Long amount, PaymentStatus status, LocalDateTime paidAt,
                               String membershipStatus, LocalDateTime membershipEndDate,
                               String userName) {
        this.paymentId = paymentId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.paidAt = paidAt;
        this.membershipStatus = membershipStatus;
        this.membershipEndDate = membershipEndDate;
        this.userName = userName;
    }
}
