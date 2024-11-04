package com.example.library_management.domain.common.notification;


import com.example.library_management.domain.common.exception.GlobalExceptionConst;
import com.example.library_management.domain.common.notification.dto.NotificationRequestDto;
import com.example.library_management.domain.common.notification.entity.Notification;
import com.example.library_management.domain.common.notification.repository.NotificationRepository;
import com.example.library_management.domain.common.notification.service.NotificationService;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private User secondUser;


    @BeforeEach
    void setUp() {
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(user, "userName", "testUser");
        ReflectionTestUtils.setField(user, "email", "duwnstj@email1.com");


        secondUser = new User();
        ReflectionTestUtils.setField(secondUser, "id", 2L);
        ReflectionTestUtils.setField(secondUser, "role", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(secondUser, "userName", "testSecondUser");
        ReflectionTestUtils.setField(secondUser, "email", "duwnstj@email2.com");
    }

    @Test
    void 이메일로_전송되지_않은_알림_전송_성공() {
        //given
        Notification notification1 = new Notification(user, "test메시지 1");
        ReflectionTestUtils.setField(notification1, "id", 1L);
        Notification notification2 = new Notification(secondUser, "test메시지 2");
        ReflectionTestUtils.setField(notification2, "id", 2L);

        given(notificationRepository.findBySentFalse()).willReturn(List.of(notification1, notification2));

        NotificationService spyNotificationService = spy(notificationService);
        //when
        spyNotificationService.sendEmailNotifications();
        //then
        verify(notificationRepository, times(1)).findBySentFalse();
        verify(spyNotificationService, times(2)).sendEmail(anyString(), anyString());
        //boolean은 getter가 is접두사를 붙여줌
        assertTrue(notification1.isSent());
        assertTrue(notification2.isSent());
    }

    @Nested
    @DisplayName("알림 생성 테스트")
    class NotificationSaveTest {

        @Test
        void 알림_생성후_저장_성공() {
            //given
            NotificationRequestDto request = new NotificationRequestDto(user.getId(), "책 반납일 1일전입니다. 반납할 준비해주세요");

            given(userRepository.findById(request.getUserId())).willReturn(Optional.of(user));

            NotificationService spyNotificationService = spy(notificationService);
            willDoNothing().given(spyNotificationService).sendEmailNotifications();
            //when
            spyNotificationService.createNotification(request);
            //then
            verify(userRepository, times(1)).findById(request.getUserId());
            verify(notificationRepository, times(1)).save(any(Notification.class));
            verify(spyNotificationService, times(1)).sendEmailNotifications();

        }

        @Test
        void 알림_생성시_해당_유저가_없을_경우() {
            //given
            NotificationRequestDto request = new NotificationRequestDto(user.getId(), "테스트메시지");
            given(userRepository.findById(request.getUserId())).willReturn(Optional.empty());
            //when&then
            NotFoundUserException exception = assertThrows(NotFoundUserException.class,
                    () -> notificationService.createNotification(request));
            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains(GlobalExceptionConst.NOT_FOUND_USER.getMessage()));
            verify(notificationRepository, never()).save(any(Notification.class));
        }
    }

}
