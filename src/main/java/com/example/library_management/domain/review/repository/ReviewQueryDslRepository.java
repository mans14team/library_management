package com.example.library_management.domain.review.repository;


import com.example.library_management.domain.review.dto.response.ReviewsGetResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewQueryDslRepository {


    Page<ReviewsGetResponse> findAllByMultipleConditions(Pageable pageable, Long bookId, Integer reviewStar);
}
