package com.example.library_management.domain.common.notification.repository;

import com.example.library_management.domain.common.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findBySentFalse();
}
