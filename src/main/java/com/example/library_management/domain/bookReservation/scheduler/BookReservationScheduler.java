package com.example.library_management.domain.bookReservation.scheduler;

import com.example.library_management.domain.bookReservation.service.BookReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookReservationScheduler {
    private final BookReservationService bookReservationService;

    // 매일 0시에 실행 (크론 표현식 사용)
    @Scheduled(cron = "0 0 0 * * *")
    public void updateReservationStates() {
        bookReservationService.expireOverdueReservations();
    }
}

