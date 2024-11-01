package com.example.library_management.domain.membership.repository;

import com.example.library_management.domain.membership.entity.Membership;
import com.example.library_management.domain.membership.enums.MembershipStatus;
import com.example.library_management.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    // 특정 사용자의 활성화된 멤버십 찾기
    Optional<Membership> findByUserAndStatus(User user, MembershipStatus status);
}
