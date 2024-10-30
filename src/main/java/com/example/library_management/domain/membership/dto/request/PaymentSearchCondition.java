package com.example.library_management.domain.membership.dto.request;

import com.example.library_management.domain.membership.enums.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class PaymentSearchCondition {
    private LocalDate startDate;
    private LocalDate endDate;
    private PaymentStatus status;
}
