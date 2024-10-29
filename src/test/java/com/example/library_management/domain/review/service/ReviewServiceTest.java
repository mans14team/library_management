package com.example.library_management.domain.review.service;

import com.example.library_management.domain.board.repository.BoardRepository;
import com.example.library_management.domain.review.dto.request.ReviewSaveRequest;
import com.example.library_management.domain.review.entity.Review;
import com.example.library_management.domain.review.repository.ReviewRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Ref;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User user;
    private User admin;
    private Review review;


    @BeforeEach
    public void setUp() {

        // 유저 설정
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(user, "userName", "testUser");

        // 관리자 설정
        admin = new User();
        ReflectionTestUtils.setField(admin, "id", 2L);
        ReflectionTestUtils.setField(admin, "role", UserRole.ROLE_ADMIN);
        ReflectionTestUtils.setField(admin, "userName", "testAdmin");

    }

    @Nested
    @DisplayName("리뷰 생성 테스트")
    class reviewCreateTest {
        @Test
        public void 리뷰_생성_성공() {

            //given
            ReviewSaveRequest saveRequest = new ReviewSaveRequest();

            ReflectionTestUtils.setField(saveRequest,"reviewStar",5);
            ReflectionTestUtils.setField(saveRequest,"reviewTitle","리뷰 제목");
            ReflectionTestUtils.setField(saveRequest,"reviewDescription","리뷰 내용");

            //when
            reviewService.saveReview();


        }
    }

}
