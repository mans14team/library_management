package com.example.library_management.domain.common.notification.service;

import com.example.library_management.domain.common.notification.dto.EmailNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationProducerService {
    // 메시지 생산자 서비스
    // 알림 이벤트를 RabbitMQ 큐에 발행하는 역할
    // RabbitTemplate을 사용하여 메시지를 지정된 exchange와 routing key로 전송
    // 비동기 처리를 위해 메시지를 큐에 넣고 즉시 반환

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.notification}")
    private String notificationExchange;

    @Value("${rabbitmq.routing.email}")
    private String emailRoutingKey;

    public void sendEmailNotification(EmailNotificationEvent event) {
        rabbitTemplate.convertAndSend(notificationExchange, emailRoutingKey, event);
        log.info("Email notification event sent to queue: {}", event);
    }
}
