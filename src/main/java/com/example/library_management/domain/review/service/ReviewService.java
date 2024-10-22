package com.example.library_management.domain.review.service;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.review.dto.request.ReviewRequest;
import com.example.library_management.domain.review.dto.request.ReviewSaveResponse;
import com.example.library_management.domain.review.dto.response.ReviewsGetResponse;
import com.example.library_management.domain.review.entity.Review;
import com.example.library_management.domain.review.exception.InvalidReviewStarException;
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
    public ReviewSaveResponse saveReview(Long bookId, ReviewRequest request, UserDetailsImpl userDetails) {

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


    //회원이 작성한 리뷰 조회
    public Page<ReviewsGetResponse> userWriteReviews(int page, int size, UserDetailsImpl userDetails) {
        Pageable pageable = PageRequest.of(page - 1, size);


       Page<Review>reviewList = reviewRepository.findAllByUserId(pageable,userDetails.getUser().getId());

       return reviewList.map(review -> new ReviewsGetResponse(
               review.getBook().getId(),
               review.getReviewStar(),
               review.getReviewTitle(),
               review.getReviewDescription(),
               review.getCreatedAt(),
               review.getModifiedAt()
       ));
    }

    //해당하는 책에 대한 리뷰 조회
    public Page<ReviewsGetResponse> getReviewsByBook(int page, int size, Long bookId, UserDetailsImpl userDetails) {
        return null;
    }
}
