package com.example.library_management.domain.common.notification.service;

import com.example.library_management.domain.common.notification.dto.NotificationRequestDto;
import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import com.example.library_management.domain.roomReserve.repository.RoomReserveRepository;
import com.example.library_management.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class StudyRoomNotifyService {
    private RoomReserveRepository roomReserveRepository;
    private NotificationService notificationService;

    // 예약일에 따른 알림을 생성하는 메서드
    public void sendReservationReminders() {
        List<RoomReserve> reservation1DaysBefore = getReservationDueIn(1);
        sendReminders(reservation1DaysBefore, "스터디룸 예약일 1일전 알림입니다.");

        List<RoomReserve> reservationDay = getReservationDueIn(0);
        sendReminders(reservationDay , "스터디룸 예약한 날입니다.");

    }

    // 해당 스터디룸을 예약한 사람들에게 보낼 메시지를 전달하는 로직
    private void sendReminders(List<RoomReserve> reservations, String message) {
        for (RoomReserve reserves : reservations) {
            User user = reserves.getUser();
            NotificationRequestDto notificationRequest = new NotificationRequestDto(user.getId(), message);

            notificationService.createNotification(notificationRequest);
        }
    }

    // 1일전 당일 예약날짜인 스터디룸 리스트 뽑아오는 로직
    private List<RoomReserve> getReservationDueIn(int dayBefore) {
        LocalDateTime targetDate = LocalDateTime.now().plusDays(dayBefore);
        return roomReserveRepository.findReservation(targetDate);
    }


}

