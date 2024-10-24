package com.example.library_management.domain.review.controller;

import com.example.library_management.domain.review.dto.request.ReviewSaveRequest;
import com.example.library_management.domain.review.dto.request.ReviewUpdateRequest;
import com.example.library_management.domain.review.dto.response.ReviewSaveResponse;
import com.example.library_management.domain.review.dto.response.ReviewUpdateResponse;
import com.example.library_management.domain.review.dto.response.ReviewsGetResponse;
import com.example.library_management.domain.review.exception.InvalidReviewStarException;
import com.example.library_management.domain.review.exception.ReviewNotFoundException;
import com.example.library_management.domain.review.service.ReviewService;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("library/books")
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 리뷰 생성
     *
     * @param bookId      :  책 고유 Id
     * @param request     : reviewStar,reviewTitle,reviewDescription - review 요청본문
     * @param userDetails : 로그인한 유저 정보
     * @return : HttpStatus(201) ,생성된 review
     * @throws InvalidReviewStarException : review 별점이 1~5가 아니라면 발생
     */
    @PostMapping("/{bookId}/reviews")
    public ResponseEntity<ReviewSaveResponse> saveReview(
            @PathVariable Long bookId,
            @RequestBody ReviewSaveRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        ReviewSaveResponse response = reviewService.saveReview(bookId, request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * reviewStar와 bookId가 존재할때 해당 책에 대한 별점을 기준으로 조회
     * 별점과 책 고유 번호가 null 값일때 리뷰 전체 조회
     * 별점만 null 일때 해당 책의 리뷰 전체 조회
     * bookId만 null일때 별점이 1~5점인 책을 조회
     *
     * @param page       : 현재 페이지
     * @param size       : 페이지 크기
     * @param bookId     : 책 고유 번호
     * @param reviewStar : 별점
     * @return response, Httpstatus(200) : 리뷰 정보 , 성공 상태메시지
     */
    @GetMapping("/reviews")
    public ResponseEntity<Page<ReviewsGetResponse>> findAllByMultipleConditions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) Integer reviewStar
    ) {
        log.info("잘 들어오는지 확인 중 ,,,");
        return ResponseEntity.ok(reviewService.findAllByMultipleConditions(page, size, bookId, reviewStar));

    }

    /**
     * 로그인한 유저가 작성한 리뷰 전체 조회
     *
     * @param userDetails : 로그인한 유저정보
     * @return : 리뷰 정보
     */
    @GetMapping("/reviews/user")
    public ResponseEntity<Page<ReviewsGetResponse>> findAllByUserWriten(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return ResponseEntity.ok(reviewService.findAllUserWriten(page, size, userDetails));
    }

    /**
     * 리뷰 수정
     *
     * @param request     : 수정할 본문
     * @param reviewId    : 수정할 리뷰Id
     * @param userDetails : 로그인한 사람 정보
     * @return : HttpStatus(200), 수정된 리뷰
     * @throws ReviewNotFoundException : 로그인한 사람이 리뷰를 작성한 사람이 아니면 예외처리
     */
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewUpdateResponse> updateReview(
            @RequestBody ReviewUpdateRequest request,
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return ResponseEntity.ok(reviewService.updateReview(request, reviewId, userDetails));
    }

    /**
     * 리뷰 삭제
     *
     * @param reviewId:   리뷰아이디
     * @param userDetails : 로그인한 유저 정보
     * @return HttpStatus(204)
     * @throws : 로그인한 사람이 리뷰를 작성한 사람이 아니면 예외처리
     */

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        reviewService.deleteReview(reviewId, userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
