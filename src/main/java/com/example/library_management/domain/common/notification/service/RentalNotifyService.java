package com.example.library_management.domain.common.notification.service;

import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.bookRental.repository.BookRentalRepository;
import com.example.library_management.domain.common.notification.dto.NotificationRequestDto;
import com.example.library_management.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RentalNotifyService {

    private final BookRentalRepository bookRentalRepository;
    private NotificationService notificationService;

   //반납 기한에 따른 알림을 생성하는 메서드
    public void sendRentalReminders(){

        List<BookRental> rentals3DaysBefore = getRentalsDueIn(3);
        sendReminders(rentals3DaysBefore,"대여 만료 3일전입니다. 책 반납 준비 해주세요!");
        List<BookRental> rentals1DaysBefore = getRentalsDueIn(1);
        sendReminders(rentals1DaysBefore , "대여 만료 1일전입니다. 책 반납 준비 해주세요!");
        List<BookRental> rentalsDaysBefore = getRentalsDueIn(0);
        sendReminders(rentalsDaysBefore,"오늘이 반납일입니다. 책 반납해주세요! ");
    }

    // 알람 생성 로직으로 알람을 보낼 사람들의 정보를 보내는 메서드
    private void sendReminders(List<BookRental> rentalList, String message) {

        for(BookRental list : rentalList){
            User user = list.getUser();
            NotificationRequestDto requestDto = new NotificationRequestDto(user.getId(),message);
            notificationService.createNotification(requestDto);
        }
    }

    // 반납 기한이 n일 후인 대여 정보를 조회하는 메서드
    private List<BookRental> getRentalsDueIn(int dayBefore) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(dayBefore); // 현재 날짜 + dayBefore
        return bookRentalRepository.findAllRentalDate(targetDate.minusDays(7));

    }

}
