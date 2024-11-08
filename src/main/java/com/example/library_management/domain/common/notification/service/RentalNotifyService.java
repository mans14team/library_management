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
import java.util.stream.Collectors;

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
        sendReminderForDueDate(rentalsDueIn3Days,3,"책 반납일 3일전입니다 책 반납 준비해주세요");
        sendReminderForDueDate(rentalsDueIn3Days,1,"책 반납일 1일전입니다. 책 반납 준비해주세요");
        sendReminderForDueDate(rentalsDueIn3Days,0,"책 반납일입니다. 책 반납해주세요");
    }

    // 대여 반납일을 기준으로 알림 보내기
    public void sendReminderForDueDate(List<BookRental> rentals, int daysBefore, String message) {

        List<BookRental> filterRentals = rentals.stream()
                .filter(bookRental -> bookRental.getRentalDate().plusDays(7)
                        .toLocalDate().isEqual(LocalDate.now().plusDays(daysBefore))
                ).toList();
        sendReminders(filterRentals, message);

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
