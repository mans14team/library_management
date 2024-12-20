package com.example.library_management.domain.common.notification;

import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.bookRental.repository.BookRentalRepository;
import com.example.library_management.domain.common.notification.service.NotificationService;
import com.example.library_management.domain.common.notification.service.RentalNotifyService;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalNotifyServiceTest {

    @Mock
    private BookRentalRepository bookRentalRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RentalNotifyService rentalNotifyService;

    private RentalNotifyService spyRentalNotifyService;


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

        spyRentalNotifyService = spy(rentalNotifyService);


    }


    @Test
    void 반납_3일전_1일전_당일_알림_생성_테스트() {
        //given
        LocalDateTime rentalDate = LocalDate.now().minusDays(4).atStartOfDay();
        BookRental rentalsDueIn3Days = new BookRental();
        ReflectionTestUtils.setField(rentalsDueIn3Days, "id", 1L);
        ReflectionTestUtils.setField(rentalsDueIn3Days, "rentalDate", rentalDate);
        ReflectionTestUtils.setField(rentalsDueIn3Days, "user", user);


        doReturn(List.of(rentalsDueIn3Days)).when(spyRentalNotifyService).getRentalsDueInRange(3);

        //when
        spyRentalNotifyService.sendRentalReminders();

        //then
        verify(spyRentalNotifyService, times(1)).getRentalsDueInRange(3);


    }

    @Test
    void 반납_기한이_n일_후인_대여정보_조회_테스트_성공() {
        //given
        int days = 3;
        LocalDateTime startDate = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime endDate = LocalDate.now().plusDays(days).atTime(LocalTime.MAX);
        //when
        rentalNotifyService.getRentalsDueInRange(days);
        //then
        verify(bookRentalRepository, times(1)).findAllRentalDateBetween(startDate, endDate);
    }


}
