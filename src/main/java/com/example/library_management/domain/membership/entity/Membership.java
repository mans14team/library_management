package com.example.library_management.domain.membership.entity;

import com.example.library_management.domain.common.entity.Timestamped;
import com.example.library_management.domain.membership.enums.MembershipStatus;
import com.example.library_management.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "membership")
public class Membership extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime startDate; // 구독 시작일

    @Column(nullable = false)
    private LocalDateTime endDate; // 구독 종료일

    private LocalDateTime nextBillingDate; // 다음 결제일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status; // 멤버십 상태 (ACTIVE, EXPIRED, CANCELLED)

    @OneToMany(mappedBy = "membership")
    private List<MembershipPayment> paymentHistory = new ArrayList<>();

    // 멤버십 상수 값들
    public static final Long MONTHLY_FEE = 10000L;  // 월 구독료
    public static final Integer MAX_RENTAL_BOOKS = 5;  // 최대 대여 가능 도서 수
    public static final Integer MAX_RESERVATION_DAYS = 14;  // 최대 예약 가능 일수

    // 멤버십 생성 메서드
    public static Membership createMembership(User user) {
        Membership membership = new Membership();
        membership.user = user;
        membership.startDate = LocalDateTime.now();
        membership.endDate = LocalDateTime.now().plusMonths(1);
        membership.nextBillingDate = membership.endDate;
        membership.status = MembershipStatus.ACTIVE;
        return membership;
    }

    // 멤버십 갱신 메서드
    public void renewMembership() {
        if (this.status != MembershipStatus.ACTIVE) {
            throw new IllegalStateException("활성화된 멤버십만 갱신할 수 있습니다.");
        }
        this.startDate = this.endDate;
        this.endDate = this.startDate.plusMonths(1);
        this.nextBillingDate = this.endDate;
    }

    // 멤버십 취소 메서드
    public void cancelMembership() {
        this.status = MembershipStatus.CANCELLED;
    }

    // 멤버십 만료 체크 메서드
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endDate);
    }
}
