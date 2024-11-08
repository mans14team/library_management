package com.example.library_management.domain.roomReserve.scheduler;

import com.example.library_management.domain.roomReserve.repository.RoomReserveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Transactional
@Component
@RequiredArgsConstructor
public class RoomReserveCleanupScheduler {

    private final RoomReserveRepository roomReserveRepository;

    // 매일 자정 전날의 예약 정보를 자동으로 삭제 (불필요하며 의미없기 때문)
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteExpiredRoomReservations() {
        // 스케쥴러 실행시점 자정의 시간 정보를 가져옴.
        LocalDateTime now = LocalDateTime.now();

        // 예약 종료 시간이 현재 시간보다 이전인 예약 정보를 삭제
        roomReserveRepository.deleteByEndTimeBefore(now);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일");
        String formattedDate = now.format(formatter);

        log.info("만료된 스터디룸 예약 정보가 {} 00시에 성공적으로 삭제되었습니다.", formattedDate);

    }
}
