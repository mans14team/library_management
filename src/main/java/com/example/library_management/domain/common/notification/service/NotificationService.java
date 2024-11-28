package com.example.library_management.domain.common.notification.service;

import com.example.library_management.domain.common.notification.dto.EmailNotificationEvent;
import com.example.library_management.domain.common.notification.dto.NotificationRequestDto;
import com.example.library_management.domain.common.notification.enums.NotificationType;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final UserRepository userRepository;
    private final NotificationProducerService producerService;

    //새로운 알림 생성 후 저장

    public void createNotification(NotificationRequestDto requestDto) {
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(NotFoundUserException::new);

        EmailNotificationEvent event = new EmailNotificationEvent(
                user.getEmail(),
                requestDto.getMessage(),
                user.getId(),
                determineNotificationType(requestDto)
        );

        producerService.sendEmailNotification(event);
    }
    // 메시지 내용을 기반으로 알림 타입 결정
    private NotificationType determineNotificationType(NotificationRequestDto requestDto) {
        if (requestDto.getMessage().contains("책 반납")) {
            return NotificationType.RENTAL_REMINDER;
        } else if (requestDto.getMessage().contains("스터디룸")) {
            return NotificationType.STUDY_ROOM_REMINDER;
        } else {
            return NotificationType.RENTAL_CONFIRMATION;
        }
    }
}
