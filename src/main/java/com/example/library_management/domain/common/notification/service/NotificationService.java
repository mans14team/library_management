package com.example.library_management.domain.common.notification.service;

import com.example.library_management.domain.common.notification.dto.NotificationRequestDto;
import com.example.library_management.domain.common.notification.entity.Notification;
import com.example.library_management.domain.common.notification.repository.NotificationRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    //새로운 알림 생성 후 저장

    public void createNotification(NotificationRequestDto requestDto) {
        log.info("알림 생성 로직 ");
        User user = userRepository.findById(requestDto.getUserId())
                .orElseThrow(NotFoundUserException::new);

        Notification notification = new Notification(user, requestDto.getMessage());

        log.info("notification  :" + notification.getUser());
        notificationRepository.save(notification);

        sendEmailNotifications();
    }

    //이메일로 전송되지 않은 알림 전송
    public void sendEmailNotifications() {
        log.info("이메일로 보내기전: 로그 확인");

        List<Notification> unNotificationList = notificationRepository.findBySentFalse();

        for (Notification notification : unNotificationList) {
            User user = notification.getUser();
            String email = user.getEmail();
            String message = notification.getMessage();

            sendEmail(email, message);

            // 보낸 메시지는 true로 변경하여 중복 메시지 송신을 방지
            notification.updateAsSent();
        }
    }

    //실제 이메일 전송 로직
    private void sendEmail(String toEmail, String message) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("도서관 알림");
        mailMessage.setText(message);

        mailSender.send(mailMessage);

        log.info("이메일을 성공적으로 보냈습니다.");
    }


}
