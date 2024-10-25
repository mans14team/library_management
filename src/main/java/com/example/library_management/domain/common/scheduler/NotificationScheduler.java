package com.example.library_management.domain.common.scheduler;

import com.example.library_management.domain.common.notification.service.RentalNotifyService;
import com.example.library_management.domain.common.notification.service.StudyRoomNotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    private final RentalNotifyService rentalNotifyService;
    private final StudyRoomNotifyService studyRoomNotifyService;

    //매일 오전 8시 30에  대여 및 스터디룸 예약 알림을 자동으로 전송
    @Scheduled(cron = "0 30 8 * * ?")
    public void sendReminders() {

        //대여 알림 전송
        rentalNotifyService.sendRentalReminders();

        //스터디룸 예약 알림 전송
        studyRoomNotifyService.sendReservationReminders();

    }
}
