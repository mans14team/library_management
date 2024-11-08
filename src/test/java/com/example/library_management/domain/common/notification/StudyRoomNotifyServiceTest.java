package com.example.library_management.domain.common.notification;


import com.example.library_management.domain.common.notification.service.NotificationService;
import com.example.library_management.domain.common.notification.service.StudyRoomNotifyService;
import com.example.library_management.domain.roomReserve.repository.RoomReserveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyRoomNotifyServiceTest {

    @Mock
    private RoomReserveRepository roomReserveRepository;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private StudyRoomNotifyService studyRoomNotifyService;


    @Test
    void 예약_1일전_및_당일_알림_생성_테스트() {
        //given

        LocalDateTime startDate = LocalDate.now().atStartOfDay();
        LocalDateTime endDate = LocalDate.now().plusDays(1).atTime(LocalTime.MAX);
        given(roomReserveRepository.findReservation(startDate, endDate)).willReturn(List.of());

        //when
        StudyRoomNotifyService spyStudyRoomNotifyService = spy(studyRoomNotifyService);
        spyStudyRoomNotifyService.sendReservationReminders();
        //then
        verify(spyStudyRoomNotifyService, times(1)).sendRemindersforDueDate(anyList(), eq(1), eq("스터디룸 예약일 1일전 알림입니다"));
        verify(spyStudyRoomNotifyService, times(1)).sendRemindersforDueDate(anyList(), eq(0), eq("스터디룸 예약일 0일전 알림입니다"));

    }
}
