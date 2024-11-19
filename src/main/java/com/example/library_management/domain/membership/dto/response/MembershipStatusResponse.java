package com.example.library_management.domain.membership.dto.response;

import com.example.library_management.domain.membership.enums.MembershipStatus;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
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
