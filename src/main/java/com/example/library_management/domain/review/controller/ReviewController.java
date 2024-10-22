package com.example.library_management.domain.review.controller;

import com.example.library_management.domain.review.dto.request.ReviewRequest;
import com.example.library_management.domain.review.dto.request.ReviewSaveResponse;
import com.example.library_management.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/books/{bookId}")
public class ReviewController {
    private final ReviewService reviewService;

    /**
     * 리뷰 생성
     *
     * @return  : 201(생성) ,reviewStar,reviewTitle,reviewDescription - review 응답본문
     * @param bookId :  책 고유 Id
     * @param request  : reviewStar,reviewTitle,reviewDescription - review 요청본문
     */
    @PostMapping("/reviews")
    public ResponseEntity<ReviewSaveResponse> saveReview(
            @PathVariable Long bookId,
            @RequestBody ReviewRequest request) {

        ReviewSaveResponse response = reviewService.saveReview(bookId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
