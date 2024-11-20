package com.example.library_management.domain.user.repository;

import com.example.library_management.domain.user.entity.User;
import com.example.library_management.global.oauth.enums.SocialType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.membership WHERE u.email = :email")
    Optional<User> findByEmailWithMembership(@Param("email") String email);

    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);

    Optional<User> findByUserName(String testUser);
}
