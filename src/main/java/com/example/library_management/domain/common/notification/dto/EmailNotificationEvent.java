package com.example.library_management.domain.common.notification.dto;

import com.example.library_management.domain.common.notification.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailNotificationEvent {
    // 알림 이벤트 DTO

    private String toEmail;
    private String message;
    private Long userId;
    private NotificationType type;
}
