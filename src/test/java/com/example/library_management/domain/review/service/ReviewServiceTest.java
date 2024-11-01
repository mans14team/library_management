package com.example.library_management.domain.review.service;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.exception.FindBookException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.common.exception.GlobalExceptionConst;
import com.example.library_management.domain.review.dto.request.ReviewSaveRequest;
import com.example.library_management.domain.review.dto.request.ReviewUpdateRequest;
import com.example.library_management.domain.review.dto.response.ReviewSaveResponse;
import com.example.library_management.domain.review.dto.response.ReviewUpdateResponse;
import com.example.library_management.domain.review.dto.response.ReviewsGetResponse;
import com.example.library_management.domain.review.entity.Review;
import com.example.library_management.domain.review.exception.InvalidReviewStarException;
import com.example.library_management.domain.review.exception.ReviewAccessForbiddenException;
import com.example.library_management.domain.review.repository.ReviewRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ReviewService reviewService;

    private UserDetailsImpl userDetailUser;
    private UserDetailsImpl userDetailAdmin;
    private Review sampleReview;
    private Book book;


    @BeforeEach
    public void setUp() {
        // 유저 설정
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(user, "userName", "testUser");
        userDetailUser = new UserDetailsImpl(user);

        // 관리자 설정
        User admin = new User();
        ReflectionTestUtils.setField(admin, "id", 2L);
        ReflectionTestUtils.setField(admin, "role", UserRole.ROLE_ADMIN);
        ReflectionTestUtils.setField(admin, "userName", "testAdmin");
        userDetailAdmin = new UserDetailsImpl(admin);
        // 책 설정
        book = new Book();
        ReflectionTestUtils.setField(book, "id", 1L);
        ReflectionTestUtils.setField(book, "bookTitle", "책 제목");
        ReflectionTestUtils.setField(book, "bookDescription", "책 내용");
        ReflectionTestUtils.setField(book, "bookAuthor", "acd");

        sampleReview = new Review(5, "리뷰 제목", "리뷰 내용", book, userDetailUser.getUser());
        ReflectionTestUtils.setField(sampleReview, "id", 1L);
    }

    @Nested
    @DisplayName("리뷰 생성 테스트")
    class reviewCreateTest {
        @Test
        void 리뷰_생성_성공() {

            //given
            ReviewSaveRequest saveRequest = new ReviewSaveRequest();
            ReflectionTestUtils.setField(saveRequest, "reviewStar", 5);
            ReflectionTestUtils.setField(saveRequest, "reviewTitle", "리뷰 제목");
            ReflectionTestUtils.setField(saveRequest, "reviewDescription", "리뷰 내용");

            sampleReview = new Review(
                    saveRequest.getReviewStar(),
                    saveRequest.getReviewTitle(),
                    saveRequest.getReviewDescription(),
                    book,
                    userDetailUser.getUser()
            );

            // bookRepository에 findById Mock 설정 추가
            given(bookRepository.findById(book.getId())).willReturn(Optional.of(book));
            given(reviewRepository.save(any(Review.class))).willReturn(sampleReview);
            //when
            ReviewSaveResponse response = reviewService.saveReview(book.getId(), saveRequest, userDetailUser);

            //then
            assertNotNull(response);
            assertEquals("리뷰 제목", response.getReviewTitle());
            assertEquals("리뷰 내용", response.getReviewDescription());
            assertEquals(5, response.getReviewStar());
            verify(reviewRepository, times(1)).save(any(Review.class));


        }

        @Test
        void 리뷰_생성시_해당하는_책이없을때_예외처리() {
            //given
            ReviewSaveRequest saveRequest = new ReviewSaveRequest();
            ReflectionTestUtils.setField(saveRequest, "reviewStar", 5);
            ReflectionTestUtils.setField(saveRequest, "reviewTitle", "리뷰 제목");
            ReflectionTestUtils.setField(saveRequest, "reviewDescription", "리뷰 내용");

            given(bookRepository.findById(book.getId())).willReturn(Optional.empty());

            //when & then
            FindBookException exception = assertThrows(FindBookException.class,
                    () -> reviewService.saveReview(1L, saveRequest, userDetailUser));

            assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains(GlobalExceptionConst.NOT_FOUND_BOOK.getMessage()));
            verify(reviewRepository, never()).save(any(Review.class));
        }

        @Test
        void 리뷰_생성시_별점이_잘못입력되었을때_예외처리() {
            //given
            ReviewSaveRequest saveRequest = new ReviewSaveRequest();
            ReflectionTestUtils.setField(saveRequest, "reviewStar", 6);
            ReflectionTestUtils.setField(saveRequest, "reviewTitle", "리뷰 제목");
            ReflectionTestUtils.setField(saveRequest, "reviewDescription", "리뷰 내용");

            given(bookRepository.findById(book.getId())).willReturn(Optional.of(book));
            //when&then
            InvalidReviewStarException exception = assertThrows(InvalidReviewStarException.class,
                    () -> reviewService.saveReview(book.getId(), saveRequest, userDetailUser));

            assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains(GlobalExceptionConst.INVALID_REVIEWSTAR.getMessage()));
            verify(reviewRepository, never()).save(any(Review.class));

        }
    }

    @Nested
    @DisplayName("리뷰 다중 조건 조회 테스트")
    class reviewGetListTest {
        @Test
        void 책과_별점조건이_없는_경우() {
            //given
            int page = 1;
            int size = 10;
            Pageable pageable = PageRequest.of(page - 1, size);
            List<Review> reviewList = createSampleReviews();
            Page<ReviewsGetResponse> reviewPage = new PageImpl<>(
                    reviewList.stream()
                            .map(reviews -> new ReviewsGetResponse(
                                    reviews.getBook().getId(),
                                    reviews.getId(),
                                    reviews.getReviewStar(),
                                    reviews.getReviewTitle(),
                                    reviews.getReviewDescription(),
                                    reviews.getCreatedAt(),
                                    reviews.getModifiedAt()

                            )).toList(),
                    pageable,
                    reviewList.size()
            );

            //bookId와 별점 조건이 없는 경우 전체 리스트 반환
            given(reviewRepository.findAllByMultipleConditions(pageable, null, null)).willReturn(reviewPage);
            //when
            Page<ReviewsGetResponse> result = reviewService.findAllByMultipleConditions(page, size, null, null);
            //then
            assertNotNull(result);
            assertEquals(reviewList.size(), result.getTotalElements());
            verify(reviewRepository, times(1)).findAllByMultipleConditions(pageable, null, null);

        }

        @Test
        void 특정_책에대한_리뷰만_조회() {
            //given
            int page = 1;
            int size = 10;
            Pageable pageable = PageRequest.of(page - 1, size);
            List<Review> reviewList = createSampleReviews();
            Page<ReviewsGetResponse> reviewPage = new PageImpl<>(
                    reviewList.stream().map(reviews -> new ReviewsGetResponse(
                            reviews.getBook().getId(),
                            reviews.getId(),
                            reviews.getReviewStar(),
                            reviews.getReviewTitle(),
                            reviews.getReviewDescription(),
                            reviews.getCreatedAt(),
                            reviews.getModifiedAt()
                    )).toList(),
                    pageable,
                    reviewList.size()
            );
            given(reviewRepository.findAllByMultipleConditions(pageable, book.getId(), null)).willReturn(reviewPage);
            //when
            Page<ReviewsGetResponse> result = reviewService.findAllByMultipleConditions(page, size, book.getId(), null);
            //then
            assertNotNull(result);
            assertEquals(reviewList.size(), result.getTotalElements());
            verify(reviewRepository, times(1)).findAllByMultipleConditions(pageable, book.getId(), null);
        }

        @Test
        void 특정_별점에_해당하는_리뷰만_조회() {
            //given
            int page = 1;
            int size = 10;
            Pageable pageable = PageRequest.of(page - 1, size);
            List<Review> reviewList = createSampleReviews();
            Page<ReviewsGetResponse> reviewPages = new PageImpl<>(
                    reviewList.stream().map(reviews -> new ReviewsGetResponse(
                            reviews.getBook().getId(),
                            reviews.getId(),
                            reviews.getReviewStar(),
                            reviews.getReviewTitle(),
                            reviews.getReviewDescription(),
                            reviews.getCreatedAt(),
                            reviews.getModifiedAt()
                    )).toList(),
                    pageable,
                    reviewList.size()
            );
            given(reviewRepository.findAllByMultipleConditions(pageable, null, 5)).willReturn(reviewPages);
            //when
            Page<ReviewsGetResponse> result = reviewService.findAllByMultipleConditions(page, size, null, 5);
            //then
            assertNotNull(result);
            assertEquals(reviewList.size(), result.getTotalElements());
            verify(reviewRepository, times(1)).findAllByMultipleConditions(pageable, null, 5);
            assertTrue(result.stream().allMatch(r -> r.getBookId() != null));
        }

        @Test
        void 별점이_1점에서_5점이_아닐_경우() {
            //given
            int page = 1;
            int size = 10;
            int reviewStar = 6;
            //when&then
            InvalidReviewStarException exception = assertThrows(InvalidReviewStarException.class,
                    () -> reviewService.findAllByMultipleConditions(page, size, book.getId(), reviewStar));
            assertNotNull(exception);
            assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains(GlobalExceptionConst.INVALID_REVIEWSTAR.getMessage()));
        }

        @Test
        void 로그인한_유저가_작성한_리뷰_조회() {
            //given
            int page = 1;
            int size = 10;
            Pageable pageable = PageRequest.of(page - 1, size);
            List<Review> reviewList = createSampleReviews();
            Page<Review> reviewPages = new PageImpl<>(
                    reviewList
                    , pageable,
                    reviewList.size()
            );
            given(reviewRepository.findAllUserWriten(pageable, userDetailUser.getUser().getId())).willReturn(reviewPages);
            //when
//            reviewService.saveReview();
            Page<ReviewsGetResponse> result = reviewService.findAllUserWriten(page, size, userDetailUser);
            //then
            assertNotNull(result);
            assertEquals(reviewList.size(), result.getTotalElements());
            verify(reviewRepository, times(1)).findAllUserWriten(pageable, userDetailUser.getUser().getId());

        }

        private List<Review> createSampleReviews() {
            List<Review> reviewList = new ArrayList<>();

            for (int i = 0; i < 10; i++) {
                Review review = new Review(
                        5,
                        "리뷰 제목" + i,
                        "리뷰 내용" + i,
                        book,
                        userDetailUser.getUser()
                );
                ReflectionTestUtils.setField(review, "id", (long) i + 1);
                reviewList.add(review);
            }
            return reviewList;
        }
    }

    @Nested
    @DisplayName("리뷰 수정 테스트")
    class reviewUpdateTest {
        @Test
        void 리뷰_수정_성공() {
            //given
            ReviewUpdateRequest request = new ReviewUpdateRequest();
            ReflectionTestUtils.setField(request, "reviewStar", 4);
            ReflectionTestUtils.setField(request, "reviewTitle", "리뷰 제목 수정");
            ReflectionTestUtils.setField(request, "reviewDescription", "리뷰 내용 수정");

            given(reviewRepository.findById(sampleReview.getId())).willReturn(Optional.of(sampleReview));

            //when
            ReviewUpdateResponse response = reviewService.updateReview(request, sampleReview.getId(), userDetailUser);

            //then
            assertNotNull(response);
            assertEquals(sampleReview.getBook().getId(), response.getBookId());
            assertEquals(sampleReview.getId(), response.getReviewId());
            assertEquals(sampleReview.getReviewStar(), response.getReviewStar());
            assertEquals(sampleReview.getReviewTitle(), response.getReviewTitle());
            assertEquals(sampleReview.getReviewDescription(), response.getReviewDescription());
        }

        @Test
        void 리뷰_수정권한이_없을경우() {
            //given
            ReviewUpdateRequest request = new ReviewUpdateRequest();
            ReflectionTestUtils.setField(request, "reviewStar", 4);
            ReflectionTestUtils.setField(request, "reviewTitle", "리뷰 제목 수정");
            ReflectionTestUtils.setField(request, "reviewDescription", "리뷰 내용 수정");


            given(reviewRepository.findById(sampleReview.getId())).willReturn(Optional.of(sampleReview));
            //when&then
            ReviewAccessForbiddenException exception = assertThrows(ReviewAccessForbiddenException.class,
                    () -> reviewService.updateReview(request, sampleReview.getId(), userDetailAdmin));
            assertNotNull(exception);
            assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains(GlobalExceptionConst.FORBIDDEN_CREATE_REVIEW.getMessage()));
        }


    }

    @Nested
    @DisplayName("리뷰 삭제 테스트")
    class ReviewDeleteTest {
        @Test
        void 리뷰_삭제_성공() {
            //given
            Long reviewId = sampleReview.getId();

            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(sampleReview));
            //when
            reviewService.deleteReview(reviewId, userDetailUser);
            //then
            verify(reviewRepository, times(1)).deleteById(reviewId);

        }

        @Test
        void 리뷰를_작성한_사람이_아닐_경우() {
            //given
            Long reviewId = sampleReview.getId();

            given(reviewRepository.findById(reviewId)).willReturn(Optional.of(sampleReview));
            //when&then
            ReviewAccessForbiddenException exception = assertThrows(
                    ReviewAccessForbiddenException.class,
                    () -> reviewService.deleteReview(reviewId, userDetailAdmin)
            );
            assertNotNull(exception);
            assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains(GlobalExceptionConst.FORBIDDEN_CREATE_REVIEW.getMessage()));
        }
    }
}
