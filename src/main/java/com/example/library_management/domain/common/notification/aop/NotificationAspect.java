package com.example.library_management.domain.common.notification.aop;

import com.example.library_management.domain.bookRental.dto.BookRentalRequestDto;
import com.example.library_management.domain.bookRental.dto.BookRentalResponseDto;
import com.example.library_management.domain.common.notification.dto.NotificationRequestDto;
import com.example.library_management.domain.common.notification.service.NotificationService;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Aspect
@Component
@Slf4j
public class NotificationAspect {
    private final NotificationService notificationService;
    private final UserRepository userRepository;

   // 책 대여 메서드 타겟
    @Pointcut("execution(* com.example.library_management.domain.bookRental.service.BookRentalService.submitBookRental(..)) && args (bookRentalRequestDto,userDetails)")
    private void serviceLayer(BookRentalRequestDto bookRentalRequestDto, UserDetails userDetails) {
    }

    @AfterReturning(pointcut = "serviceLayer(bookRentalRequestDto,userDetails)", returning = "response", argNames = "response,bookRentalRequestDto,userDetails")
    public void sendRentalNotification(BookRentalResponseDto response,
                                       BookRentalRequestDto bookRentalRequestDto,
                                       UserDetails userDetails) {

        Long userId = bookRentalRequestDto.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(NotFoundUserException::new);
        String email = user.getEmail();
        String message = "안녕하세요! " + user.getUserName() + "님 책 대여가 완료되었습니다! ";

        // NotificationRequestDto를 통해 알림 요청
        NotificationRequestDto requestDto = new NotificationRequestDto(userId, message);
        notificationService.createNotification(requestDto);

        log.info("대여 알림 전송 완료 ! :{}", email);
    }
}
