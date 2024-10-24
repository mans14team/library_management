package com.example.library_management.domain.review.repository;


import com.example.library_management.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryDslRepository {


    @Query("select r from Review r where r.user.id =:userId")
    Page<Review> findAllUserWriten(Pageable pageable, @Param("userId") Long userId);
}





