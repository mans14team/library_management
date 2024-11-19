package com.example.library_management.domain.common.notification.service;

import com.example.library_management.domain.common.notification.dto.EmailNotificationEvent;
import com.example.library_management.domain.common.notification.entity.Notification;
import com.example.library_management.domain.common.notification.repository.NotificationRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumerService {
    // 메시지 소비자 서비스
    // RabbitMQ 큐에서 메시지를 수신하여 처리
    // @RabbitListener를 통해 큐의 메시지를 자동으로 수신
    // 실제 이메일 발송 처리
    // 알림 내역을 DB에 저장
    // 에러 발생시 예외 처리 및 Dead Letter Queue로 메시지 이동

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @RabbitListener(queues = "${rabbitmq.queue.email}")
    public void handleEmailNotification(EmailNotificationEvent event) {
        log.info("Received email notification event: {}", event);
        try {
            sendEmail(event.getToEmail(), event.getMessage());
            saveNotification(event);
        } catch (Exception e) {
            log.error("Failed to process email notification: {}", e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Failed to process notification", e);
        }
    }

    private void sendEmail(String toEmail, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("도서관 알림");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
        log.info("Email sent successfully to: {}", toEmail);
    }

    private void saveNotification(EmailNotificationEvent event) {
        User user = userRepository.findById(event.getUserId())
                .orElseThrow(NotFoundUserException::new);
        Notification notification = new Notification(user, event.getMessage());
        notification.updateAsSent();
        notificationRepository.save(notification);
    }
}
