package com.example.library_management.domain.membership.repository;

import com.example.library_management.domain.membership.dto.request.PaymentSearchCondition;
import com.example.library_management.domain.membership.dto.response.PaymentSearchResult;
import com.example.library_management.domain.membership.dto.response.QPaymentSearchResult;
import com.example.library_management.domain.membership.enums.PaymentStatus;
import com.example.library_management.domain.user.entity.QUser;
import com.example.library_management.domain.user.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.library_management.domain.membership.entity.QMembership.membership;
import static com.example.library_management.domain.membership.entity.QMembershipPayment.membershipPayment;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryCustomImpl implements PaymentRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PaymentSearchResult> search(
            PaymentSearchCondition condition,
            User user,
            Pageable pageable) {

        // 검색 결과 조회
        List<PaymentSearchResult> content = queryFactory
                .select(new QPaymentSearchResult(
                        membershipPayment.id,
                        membershipPayment.paymentKey,
                        membershipPayment.orderId,
                        membershipPayment.amount,
                        membershipPayment.status,
                        membershipPayment.paidAt,
                        membershipPayment.membership.status.stringValue(),
                        membershipPayment.membership.endDate,
                        membershipPayment.membership.user.userName
                ))
                .from(membershipPayment)
                .join(membershipPayment.membership, membership)
                .join(membership.user, QUser.user)
                .where(
                        membership.user.eq(user),
                        dateRangeFilter(condition.getStartDate(), condition.getEndDate()),
                        statusFilter(condition.getStatus())
                )
                .orderBy(
                        membershipPayment.paidAt.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(membershipPayment.count())
                .from(membershipPayment)
                .join(membershipPayment.membership, membership)
                .where(
                        membership.user.eq(user),
                        dateRangeFilter(condition.getStartDate(), condition.getEndDate()),
                        statusFilter(condition.getStatus())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 날짜 범위 필터
    private BooleanExpression dateRangeFilter(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }

        LocalDateTime start = startDate != null ?
                startDate.atStartOfDay() : LocalDateTime.now().minusMonths(3);
        LocalDateTime end = endDate != null ?
                endDate.atTime(23, 59, 59) : LocalDateTime.now();

        return membershipPayment.paidAt.between(start, end);
    }

    // 결제 상태 필터
    private BooleanExpression statusFilter(PaymentStatus status) {
        return status != null ? membershipPayment.status.eq(status) : null;
    }
}
