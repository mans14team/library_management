package com.example.library_management.domain.common.notification;


import com.example.library_management.domain.common.notification.service.NotificationService;
import com.example.library_management.domain.common.notification.service.StudyRoomNotifyService;
import com.example.library_management.domain.roomReserve.repository.RoomReserveRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;



class StudyRoomNotifyServiceTest {

    @Mock
    private RoomReserveRepository roomReserveRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private StudyRoomNotifyService studyRoomNotifyService;


}
