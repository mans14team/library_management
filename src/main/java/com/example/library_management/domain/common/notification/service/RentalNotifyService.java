package com.example.library_management.domain.common.notification.service;

import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.bookRental.repository.BookRentalRepository;
import com.example.library_management.domain.common.notification.dto.NotificationRequestDto;
import com.example.library_management.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class RentalNotifyService {

    private final BookRentalRepository bookRentalRepository;
    private final NotificationService notificationService;

    //반납 기한에 따른 알림을 생성하는 메서드
    public void sendRentalReminders() {

        List<BookRental> rentalsDueIn3Days = getRentalsDueInRange(3);

        List<BookRental> rentals3DaysBefore = rentalsDueIn3Days.stream()  // 메서드로 변환
                .filter(bookRental -> bookRental.getRentalDate().plusDays(7)
                        .toLocalDate().isEqual(LocalDate.now().plusDays(3)))
                .toList();

        sendReminders(rentals3DaysBefore, "대여 만료 3일전입니다.책 반납 준비 해주세요!");

        List<BookRental> rentals1DaysBefore = rentalsDueIn3Days.stream()
                .filter(bookRental -> bookRental.getRentalDate().plusDays(7)
                        .toLocalDate().isEqual(LocalDate.now().plusDays(1)))
                .toList();

        sendReminders(rentals1DaysBefore, "대여 만료 1일전입니다. 책 반납 준비 해주세요!");

        List<BookRental> rentalsDaysBefore = rentalsDueIn3Days.stream()
                .filter(bookRental -> bookRental.getRentalDate().plusDays(7)
                        .toLocalDate().isEqual(LocalDate.now()))
                .toList();
        sendReminders(rentalsDaysBefore, "오늘이 반납일입니다. 책 반납해주세요! ");
    }


    // 알람 생성 로직으로 알람을 보낼 사람들의 정보를 보내는 메서드
    public void sendReminders(List<BookRental> rentalList, String message) {


        for (BookRental list : rentalList) {
            User user = list.getUser();
            NotificationRequestDto requestDto = new NotificationRequestDto(user.getId(), message);
            notificationService.createNotification(requestDto);

        }
    }

    // 반납 기한이 n일 후인 대여 정보를 조회하는 메서드
    public List<BookRental> getRentalsDueInRange(int days) {
        LocalDateTime startDate = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime endDate = LocalDate.now().plusDays(days).atTime(LocalTime.MAX); // 현재 날짜 + dayBefore
        return bookRentalRepository.findAllRentalDateBetween(startDate, endDate);

    }

}
