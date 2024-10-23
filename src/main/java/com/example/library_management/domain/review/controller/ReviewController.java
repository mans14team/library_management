package com.example.library_management.domain.review.controller;

import com.example.library_management.domain.review.dto.request.ReviewRequest;
import com.example.library_management.domain.review.dto.request.ReviewSaveResponse;
import com.example.library_management.domain.review.dto.response.ReviewsGetResponse;
import com.example.library_management.domain.review.exception.InvalidReviewStarException;
import com.example.library_management.domain.review.service.ReviewService;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/books")
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 리뷰 생성
     *
     * @param bookId      :  책 고유 Id
     * @param request     : reviewStar,reviewTitle,reviewDescription - review 요청본문
     * @param userDetails : 로그인한 유저 정보
     * @return : 201(생성) ,reviewStar,reviewTitle,reviewDescription - review 응답본문
     * @throws InvalidReviewStarException : review 별점이 1~5가 아니라면 발생
     */
    @PostMapping("/{bookId}/reviews")
    public ResponseEntity<ReviewSaveResponse> saveReview(
            @PathVariable Long bookId,
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ReviewSaveResponse response = reviewService.saveReview(bookId, request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     *
     * reviewStar와 bookId가 존재할때 해당 책에 대한 별점을 기준으로 조회
     * 별점과 책 고유 번호가 null 값일때 리뷰 전체 조회
     * 별점만 null 일때 해당 책의 리뷰 전체 조회
     * bookId만 null일때 별점이 1~5점인 책을 조회
     *
     * @param page        : 현재 페이지
     * @param size        : 페이지 크기
     * @param bookId      : 책 고유 번호
     * @param reviewStar : 별점
     */
    @GetMapping("/reviews")
    public ResponseEntity<Page<ReviewsGetResponse>> findAllByMultipleConditions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Integer reviewStar
    ) {
        return  ResponseEntity.ok(reviewService.findAllByMultipleConditions(page,size,bookId,reviewStar));

    }
}
