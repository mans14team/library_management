package com.example.library_management.domain.review.entity;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.common.entity.Timestamped;
import com.example.library_management.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(
        //별점에 단일 인덱스 적용,bookid와 별점 복한 인덱스 적용
        name = "review",
        indexes = {
                @Index(name = "idx_book_review_star", columnList = "book_id,reviewStar")
        }
)
public class Review extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer reviewStar;
    @Column(nullable = false)
    private String reviewTitle;
    @Column(nullable = false)
    private String reviewDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Review(Integer reviewStar, String reviewTitle, String reviewDescription, Book book, User user) {
        this.reviewStar = reviewStar;
        this.reviewTitle = reviewTitle;
        this.reviewDescription = reviewDescription;
        this.book = book;
        this.user = user;
    }

    public void update(Integer reviewStar, String reviewTitle, String reviewDescription) {
        this.reviewStar = reviewStar;
        this.reviewTitle = reviewTitle;
        this.reviewDescription = reviewDescription;
    }
}



