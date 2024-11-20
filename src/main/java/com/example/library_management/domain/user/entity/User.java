package com.example.library_management.domain.user.entity;

import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.common.entity.Timestamped;
import com.example.library_management.domain.membership.entity.Membership;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.domain.user.enums.UserStatus;
import com.example.library_management.global.oauth.enums.SocialType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;
    private String userName;

    @Column(length = 30)
    private String phone;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    // 소셜 로그인 관련 필드 추가
    @Enumerated(EnumType.STRING)
    private SocialType socialType; // KAKAO, GOOGLE 등

    private String socialId; // 소셜 식별자

    @OneToOne(mappedBy = "user")
    private Membership membership;

    @OneToMany(mappedBy = "user")
    private List<BookRental> bookRentalList = new ArrayList<>();

    public User(String email, String password, String userName, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.role = userRole;
        this.status = UserStatus.ACTIVE;
    }

    // 기존 생성자는 유지하고 새로운 생성자 추가
    public User(String email, String password, String userName, UserRole userRole,
                SocialType socialType, String socialId) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.role = userRole;
        this.status = UserStatus.ACTIVE;
        this.socialType = socialType;
        this.socialId = socialId;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void delete() {
        this.status = UserStatus.DELETED;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    // 소셜 계정 연동 메서드
    public void connectSocialAccount(SocialType socialType, String socialId) {
        this.socialType = socialType;
        this.socialId = socialId;
    }
}
