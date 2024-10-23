package com.example.library_management.domain.review.repository;


import com.example.library_management.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryDslRepository {


}





