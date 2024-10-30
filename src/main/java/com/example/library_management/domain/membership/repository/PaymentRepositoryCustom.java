package com.example.library_management.domain.membership.repository;

import com.example.library_management.domain.membership.dto.request.PaymentSearchCondition;
import com.example.library_management.domain.membership.dto.response.PaymentSearchResult;
import com.example.library_management.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentRepositoryCustom {
    Page<PaymentSearchResult> search(PaymentSearchCondition condition, User user, Pageable pageable);
}
