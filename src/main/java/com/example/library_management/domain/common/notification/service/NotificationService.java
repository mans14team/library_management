package com.example.library_management.domain.common.notification.service;

import com.example.library_management.domain.common.notification.dto.EmailNotificationEvent;
import com.example.library_management.domain.common.notification.dto.NotificationRequestDto;
import com.example.library_management.domain.common.notification.entity.Notification;
import com.example.library_management.domain.common.notification.enums.NotificationType;
import com.example.library_management.domain.common.notification.repository.NotificationRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
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

    private NotificationType determineNotificationType(NotificationRequestDto requestDto) {
        // 메시지 내용을 기반으로 알림 타입 결정
        if (requestDto.getMessage().contains("책 반납")) {
            return NotificationType.RENTAL_REMINDER;
        } else if (requestDto.getMessage().contains("스터디룸")) {
            return NotificationType.STUDY_ROOM_REMINDER;
        } else {
            return NotificationType.RENTAL_CONFIRMATION;
        }
    }
    //    //이메일로 전송되지 않은 알림 전송
//    public void sendEmailNotifications() {
//
//
//        List<Notification> unNotificationList = notificationRepository.findBySentFalse();
//
//        for (Notification notification : unNotificationList) {
//            User user = notification.getUser();
//            String email = user.getEmail();
//            String message = notification.getMessage();
//
//            sendEmail(email, message);
//
//            // 보낸 메시지는 true로 변경하여 중복 메시지 송신을 방지
//            notification.updateAsSent();
//        }
//    }
//
//    //실제 이메일 전송 로직
//    @Async
//    public void sendEmail(String toEmail, String message) {
//    try {
//        SimpleMailMessage mailMessage = new SimpleMailMessage();
//        mailMessage.setTo(toEmail);
//        mailMessage.setSubject("도서관 알림");
//        mailMessage.setText(message);
//
//        mailSender.send(mailMessage);
//
//        log.info("이메일을 성공적으로 보냈습니다.");
//    }catch (MailException e){
//        log.error("이메일 전송 에러 발생: {}", e.getMessage(),e);
//        }
//    }
}
