package com.example.library_management.domain.review.service;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.review.dto.request.ReviewRequest;
import com.example.library_management.domain.review.dto.request.ReviewSaveResponse;
import com.example.library_management.domain.review.entity.Review;
import com.example.library_management.domain.review.exception.InvalidReviewStarException;
import com.example.library_management.domain.review.repository.ReviewRepository;
import com.example.library_management.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    public ReviewSaveResponse saveReview(Long bookId, ReviewRequest request) {

        // 책이 등록되어있지 않다면 예외처리
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 책을 찾을 수 없습니다."));

        // ReviewStar가 1~5점이 아니면 예외처리
        if (!(request.getReviewStar() >= 1 && request.getReviewStar() <= 5)) {
            throw new InvalidReviewStarException();
        }
        Review review = new Review(
                request.getReviewStar(),
                request.getReviewTitle(),
                request.getReviewDescription(),
                book
        );
        Review saveReview = reviewRepository.save(review);

        return new ReviewSaveResponse(
                saveReview.getReviewStar(),
                saveReview.getReviewTitle(),
                saveReview.getReviewDescription()
        );


    }
}
