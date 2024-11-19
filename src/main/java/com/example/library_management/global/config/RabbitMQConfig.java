package com.example.library_management.global.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    // Queue, Exchange, Binding 설정
    // Dead Letter Queue 설정 (메시지 처리 실패시 저장되는 큐)
    // Message Converter 설정 (JSON 형식으로 메시지 변환)

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private int port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${rabbitmq.queue.email}")
    private String emailQueue;

    @Value("${rabbitmq.exchange.notification}")
    private String notificationExchange;

    @Value("${rabbitmq.routing.email}")
    private String emailRoutingKey;

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        return connectionFactory;
    }

    // 이메일 알림 메시지가 저장되는 큐
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(emailQueue)
                .deadLetterExchange(notificationExchange + ".dlx")
                .deadLetterRoutingKey(emailRoutingKey + ".dlq")
                .build();
    }

    // 처리 실패한 메시지가 저장되는 큐
    @Bean
    public Queue deadLetterQueue() {
        return new Queue(emailQueue + ".dlq");
    }

    // 메시지 큐로 라우팅하는 교환기
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(notificationExchange);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(notificationExchange + ".dlx");
    }

    // Binding : Exchange와 Queue를 연결하는 규칙
    @Bean
    public Binding emailBinding() {
        return BindingBuilder
                .bind(emailQueue())
                .to(notificationExchange())
                .with(emailRoutingKey);
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder
                .bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(emailRoutingKey + ".dlq");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
