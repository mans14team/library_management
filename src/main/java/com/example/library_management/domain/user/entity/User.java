package com.example.library_management.domain.user.entity;

import com.example.library_management.domain.common.entity.Timestamped;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.domain.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public User(String email, String password, String userName, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userName = userName;
        this.role = userRole;
        this.status = UserStatus.ACTIVE;
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
}
