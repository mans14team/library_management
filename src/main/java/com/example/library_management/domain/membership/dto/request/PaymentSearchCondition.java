package com.example.library_management.domain.membership.dto.request;

import com.example.library_management.domain.membership.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@Builder
public class PaymentSearchCondition {
    private LocalDate startDate;
    private LocalDate endDate;
    private PaymentStatus status;
}
