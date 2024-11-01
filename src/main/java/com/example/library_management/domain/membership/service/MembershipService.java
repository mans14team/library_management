package com.example.library_management.domain.membership.service;

import com.example.library_management.domain.membership.dto.response.MembershipStatusResponse;
import com.example.library_management.domain.membership.entity.Membership;
import com.example.library_management.domain.membership.enums.MembershipStatus;
import com.example.library_management.domain.membership.exception.MembershipNotFoundException;
import com.example.library_management.domain.membership.repository.MembershipRepository;
import com.example.library_management.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipService {
    private final MembershipRepository membershipRepository;

    // 멤버십 상태 조회
    @Transactional(readOnly = true)
    public MembershipStatusResponse getMembershipStatus(User user) {
        Membership membership = membershipRepository.findByUserAndStatus(user, MembershipStatus.ACTIVE)
                .orElseThrow(() -> new MembershipNotFoundException());

        // 멤버십 만료까지 남은 일수 계산
        long remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), membership.getEndDate());

        return MembershipStatusResponse.builder()
                .membershipId(membership.getId())
                .status(membership.getStatus())
                .startDate(membership.getStartDate())
                .endDate(membership.getEndDate())
                .nextBillingDate(membership.getNextBillingDate())
                .remainingDays(remainingDays)
                .build();
    }
}
