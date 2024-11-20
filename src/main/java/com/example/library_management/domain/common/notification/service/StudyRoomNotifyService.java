package com.example.library_management.domain.common.notification.service;

import com.example.library_management.domain.common.notification.dto.NotificationRequestDto;
import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import com.example.library_management.domain.roomReserve.repository.RoomReserveRepository;
import com.example.library_management.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyRoomNotifyService {
    private final RoomReserveRepository roomReserveRepository;
    private final NotificationService notificationService;

    // 예약일에 따른 알림을 생성하는 메서드
    public void sendReservationReminders() {
        LocalDateTime startDate = LocalDate.now().atStartOfDay();
        LocalDateTime endDate = LocalDate.now().plusDays(1).atTime(LocalTime.MAX);

        //필요한 예약일 리스트 전부 가져오기
        List<RoomReserve> allReservations = roomReserveRepository.findReservation(startDate, endDate);
        sendRemindersforDueDate(allReservations, 1, "스터디룸 예약일 1일전 알림입니다");
        sendRemindersforDueDate(allReservations, 0, "스터디룸 예약일 0일전 알림입니다");


    }

    // 예약일에 따라 필터링하는 메서드
    public void sendRemindersforDueDate(List<RoomReserve> reserves, int dayBefore, String message) {
        List<RoomReserve> filterReserves = reserves.stream()
                .filter(roomReserve -> roomReserve.getReservationDateEnd()
                        .toLocalDate().isEqual(LocalDate.now().plusDays(dayBefore))
                ).toList();
        sendReminders(filterReserves, message);
    }

    // 필터링을 해서 해당하는 날짜의 자정시간~끝까지 조회
    private boolean isDueIn(RoomReserve reservation, int daysBefore) {
        LocalDateTime targetStart = LocalDate.now().plusDays(daysBefore).atStartOfDay();
        LocalDateTime targetEnd = LocalDate.now().plusDays(daysBefore).atTime(LocalTime.MAX);

        return !reservation.getReservationDateEnd().isBefore(targetStart) &&
                !reservation.getReservationDateEnd().isAfter(targetEnd);
    }

    // 해당 스터디룸을 예약한 사람들에게 보낼 메시지를 전달하는 로직
    private void sendReminders(List<RoomReserve> reservations, String message) {
        for (RoomReserve reserves : reservations) {
            User user = reserves.getUser();
            NotificationRequestDto notificationRequest = new NotificationRequestDto(user.getId(), message);

            notificationService.createNotification(notificationRequest);
        }
    }


}

