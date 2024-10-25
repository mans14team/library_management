package com.example.library_management.domain.common.notification.entity;

import com.example.library_management.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;  // 알림 메시지
    private boolean sent;  // 알림 전송 여부
    private LocalDateTime timestamp; // 알림 생성 시간

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user ;


    public Notification(User user, String message) {
        this.user = user;
        this.message = message;
    }

    public void updateAsSent() {
        this.sent = true;
    }
}
