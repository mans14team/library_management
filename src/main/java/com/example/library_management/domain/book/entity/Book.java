package com.example.library_management.domain.book.entity;

import com.example.library_management.domain.bookCategory.entity.BookCategory;
import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.example.library_management.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bookTitle;

    private String bookDescription;
    @Column(nullable = false)
    private String bookPublisher;
    @Column(nullable = false)
    private LocalDateTime bookPublished;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private BookCategory category;

    @OneToMany(mappedBy = "book")
    private List<BookCopy> copyList = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<Review> reviewList = new ArrayList<>();
}
