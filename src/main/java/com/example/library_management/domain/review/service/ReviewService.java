package com.example.library_management.domain.review.service;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.review.dto.request.ReviewSaveRequest;
import com.example.library_management.domain.review.dto.request.ReviewUpdateRequest;
import com.example.library_management.domain.review.dto.response.ReviewSaveResponse;
import com.example.library_management.domain.review.dto.response.ReviewUpdateResponse;
import com.example.library_management.domain.review.dto.response.ReviewsGetResponse;
import com.example.library_management.domain.review.entity.Review;
import com.example.library_management.domain.review.exception.InvalidReviewStarException;
import com.example.library_management.domain.review.exception.ReviewAccessForbiddenException;
import com.example.library_management.domain.review.exception.ReviewNotFoundException;
import com.example.library_management.domain.review.repository.ReviewRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.domain.user.repository.UserRepository;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReviewSaveResponse saveReview(Long bookId, ReviewSaveRequest request, UserDetailsImpl userDetails) {

        // 책이 등록되어있지 않다면 예외처리
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 책을 찾을 수 없습니다."));

        //회원이 아니라면 예외처리
        User user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(NotFoundUserException::new);

        // ReviewStar가 1~5점이 아니면 예외처리
        if (!(request.getReviewStar() >= 1 && request.getReviewStar() <= 5)) {
            throw new InvalidReviewStarException();
        }
        Review review = new Review(
                request.getReviewStar(),
                request.getReviewTitle(),
                request.getReviewDescription(),
                book,
                user
        );
        Review saveReview = reviewRepository.save(review);

        return new ReviewSaveResponse(
                saveReview.getReviewStar(),
                saveReview.getReviewTitle(),
                saveReview.getReviewDescription()
        );


    }

    // 조건에 맞는 리뷰 리스트 조회
    public Page<ReviewsGetResponse> findAllByMultipleConditions(int page, int size, Long bookId, Integer reviewStar) {
        Pageable pageable = PageRequest.of(page - 1, size);


        // 별점이 null 이 아닐 경우에만 유효 범위 검증
        if (reviewStar != null && !(reviewStar >= 1 && reviewStar <= 5)) {
            throw new InvalidReviewStarException();
        }

        // 별점이 null이면 그책에 해당하는 전체 별점을 조회하고 값이 있으면 해당 별점만 조회
        return reviewRepository.findAllByMultipleConditions(
                pageable, bookId, reviewStar);


    }

    //로그인한 유저가 작성한 리뷰 조회
    public Page<ReviewsGetResponse> findAllUserWriten(int page, int size, UserDetailsImpl userDetails) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Review> reviewList = reviewRepository.findAllUserWriten(pageable, userDetails.getUser().getId());

        return reviewList.map(list -> new ReviewsGetResponse(
                list.getBook().getId(),
                list.getId(),
                list.getReviewStar(),
                list.getReviewTitle(),
                list.getReviewDescription(),
                list.getCreatedAt(),
                list.getModifiedAt()
        ));

    }

    @Transactional
    public ReviewUpdateResponse updateReview(ReviewUpdateRequest request, Long reviewId, UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();

        //리뷰가 없을때 예외처리
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
        // 리뷰 수정 권한이 없다면 예외처리
        if (!review.getUser().getId().equals(userId)) {
            throw new ReviewAccessForbiddenException();
        }

        review.update(
                request.getReviewStar(),
                request.getReviewTitle(),
                request.getReviewDescription()
        );
        return new ReviewUpdateResponse(
                review.getBook().getId(),
                review.getId(),
                review.getReviewStar(),
                review.getReviewTitle(),
                review.getReviewDescription(),
                review.getCreatedAt(),
                review.getModifiedAt()
        );
    }

    @Transactional
    public void deleteReview(Long reviewId, UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getId();

        //리뷰가 없을 때 예외처리
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
        // 리뷰를 작성한 사람이 아니면 예외처리
        if (!review.getUser().getId().equals(userId)) {
            throw new ReviewAccessForbiddenException();
        }
        reviewRepository.deleteById(reviewId);
    }
}
