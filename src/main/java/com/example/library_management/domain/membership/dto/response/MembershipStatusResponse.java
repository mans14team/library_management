package com.example.library_management.domain.membership.dto.response;

import com.example.library_management.domain.membership.enums.MembershipStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MembershipStatusResponse {
    private Long membershipId;
    private MembershipStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime nextBillingDate;
    private long remainingDays;                   // 만료까지 남은 일수
}
