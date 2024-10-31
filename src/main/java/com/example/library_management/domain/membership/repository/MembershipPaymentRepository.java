package com.example.library_management.domain.membership.repository;

import com.example.library_management.domain.membership.entity.Membership;
import com.example.library_management.domain.membership.entity.MembershipPayment;
import com.example.library_management.domain.membership.enums.PaymentStatus;
import com.example.library_management.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MembershipPaymentRepository extends JpaRepository<MembershipPayment, Long>, PaymentRepositoryCustom {
    // 주문번호로 결제 내역 찾기
    Optional<MembershipPayment> findByPaymentKey(String paymentKey);

    // 사용자별 결제 내역 조회 ( 페이징 )
    @Query("SELECT mp FROM MembershipPayment mp " +
            "JOIN mp.membership m " +
            "WHERE m.user = :user " +
            "AND mp.paidAt BETWEEN :startDateTime AND :endDateTime " +
            "AND (:status IS NULL OR mp.status = :status)")
    Page<MembershipPayment> findPaymentHistory(
            @Param("user") User user,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("status") PaymentStatus status,
            Pageable pageable);

    // 특정 멤버십의 가장 최근 결제 정보를 조회
    @Query("SELECT mp FROM MembershipPayment mp WHERE mp.membership = :membership ORDER BY mp.createdAt DESC")
    Optional<MembershipPayment> findTopByMembershipOrderByCreatedAtDesc(@Param("membership") Membership activeMembership);
}
