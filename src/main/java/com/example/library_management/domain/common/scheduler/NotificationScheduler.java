package com.example.library_management.domain.common.scheduler;

import com.example.library_management.domain.common.notification.service.RentalNotifyService;
import com.example.library_management.domain.common.notification.service.StudyRoomNotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final RentalNotifyService rentalNotifyService;
    private final StudyRoomNotifyService studyRoomNotifyService;
    private final RedissonClient redissonClient;

    //매일 오전 8시 30에  대여 및 스터디룸 예약 알림을 자동으로 전송
    @Scheduled(cron = "0 30 8 * * ?")
    public void sendReminders() {


        RLock lock = redissonClient.getLock("NotificationSchedulerLock");
        try {
            //watchDog로 락 갱신 연장
            lock.lock(10, TimeUnit.MINUTES);
            //대여 알림 전송
            rentalNotifyService.sendRentalReminders();
            //스터디룸 예약 알림 전송
            studyRoomNotifyService.sendReservationReminders();
        } catch (Exception e) {
            log.error("알림 전송 중 에러가 발생했습니다. ", e);
        } finally {
            if (lock.isHeldByCurrentThread()) {// 현재 스레드가 락을 보유하고 있는 지 확인
                lock.unlock(); // 락을 보유하고 있다면 락 해제
            }

        }
    }

//    @Scheduled(fixedRate = 10000) // 10초마다 실행
//    public void sendTestReminders() {
//        // 테스트용 알림 전송 코드
//        log.info("테스트 알림 전송 중...");
//
//        // 실제 알림 전송 로직
//
//        //대여 알림 전송
//        log.info("대여 알림 메시지 생성");
//        rentalNotifyService.sendRentalReminders();
//
//        //스터디룸 예약 알림 전송
//        studyRoomNotifyService.sendReservationReminders();
//
//    }
}
