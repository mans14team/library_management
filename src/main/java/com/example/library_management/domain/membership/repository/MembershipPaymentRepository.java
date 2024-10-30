package com.example.library_management.domain.membership.repository;

import com.example.library_management.domain.membership.entity.MembershipPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipPaymentRepository extends JpaRepository<MembershipPayment, Long> {
    // 주문번호로 결제 내역 찾기
    Optional<MembershipPayment> findByPaymentKey(String paymentKey);
}
