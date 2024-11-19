package com.example.library_management.domain.common.notification.aop;

import com.example.library_management.domain.bookRental.dto.BookRentalRequestDto;
import com.example.library_management.domain.bookRental.dto.BookRentalResponseDto;
import com.example.library_management.domain.common.notification.service.NotificationService;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
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

        notificationService.sendEmail(email, message);

        log.info("대여 알림 전송 완료 ! :{}", email);

    }

    @AfterThrowing(pointcut = "serviceLayer(bookRentalRequestDto,userDetails)", throwing = "ex", argNames = "ex,bookRentalRequestDto,userDetails")
    public void sendRentalNotification(Exception ex,
                                       BookRentalRequestDto bookRentalRequestDto,
                                       UserDetails userDetails) {
        log.error("대여 알림 송신 중 예외 발생 ! :{},요청한 userId:{},도서관 관리자 계정:{}", ex.getMessage(), bookRentalRequestDto.getUserId(), userDetails.getUsername(), ex);

        try {
            String userName = userDetails.getUsername();

            User user = userRepository.findByUserName(userName).orElseThrow(NotFoundUserException::new);

            String adminUserEmail = user.getEmail();
            String message = new StringBuilder()
                    .append("안녕하세요 도서관 관리자님.\n")
                    .append("대여 알림 송신 중 예외가 발생했습니다.\n")
                    .append("문제를 해결하기 위해 개발자에게 문의해주세요.")
                    .toString();

            //도서관 관리자 이메일로 전송
            notificationService.sendEmail(adminUserEmail, message);
            log.info("도서관 관리자에게 예외 메시지 전달 완료 : {}", adminUserEmail);
        } catch (Exception emailException) {
            // 이메일 전송 실패시 로그 작성
            log.error("관리자에게 이메일 송신 중 추가 에러 발생 : {}", emailException.getMessage(), emailException);
        }

    }
}
