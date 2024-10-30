package com.example.library_management.domain.membership.entity;

import com.example.library_management.domain.common.entity.Timestamped;
import com.example.library_management.domain.membership.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "membership_payments")
public class MembershipPayment extends Timestamped {    // 결제 정보 저장하는 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentKey; // 토스 페이먼츠 결제 키 ( 결제 성공시에만 존재)

    @Column(nullable = false)
    private String orderId;  // 주문번호
    
    @Column(nullable = false)
    private Long amount; // 결제 금액
    
    @Column(nullable = false)
    private LocalDateTime paidAt; // 결제 일시
    
    private String billingKey; // 자동결제를 위한 빌링키
    
    @Column(length = 500)
    private String failReason; // 결제 실패 사유

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;  // 결제 상태 (SUCCESS, FAILED, CANCELLED)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id")
    private Membership membership;

    // 결제 생성 메서드
    public static MembershipPayment createPayment(
            Membership membership,
            String paymentKey,
            String billingKey,
            String orderId) {
        MembershipPayment payment = new MembershipPayment();
        payment.membership = membership;
        payment.paymentKey = paymentKey;
        payment.orderId = orderId;
        payment.amount = Membership.MONTHLY_FEE;
        payment.paidAt = LocalDateTime.now();
        payment.status = PaymentStatus.SUCCESS;
        payment.billingKey = billingKey;
        return payment;
    }

    // 추가: 실패한 결제 생성을 위한 빌더
    @Builder
    public MembershipPayment(String orderId, PaymentStatus status,
                             String failReason, Long amount, LocalDateTime paidAt) {
        this.orderId = orderId;
        this.status = status;
        this.failReason = failReason;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    // 결제 실패 처리 메서드
    public void markAsFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failReason = reason;
    }

    // 결제 취소 처리 메서드
    public void markAsCancelled(String reason) {
        this.status = PaymentStatus.CANCELLED;
        this.failReason = reason;
    }
}
