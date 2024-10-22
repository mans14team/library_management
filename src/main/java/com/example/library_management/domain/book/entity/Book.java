package com.example.library_management.domain.book.entity;

import com.example.library_management.domain.bookCategory.entity.BookCategory;
import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.example.library_management.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String bookTitle;

    private String bookDescription;

    private String bookAuthor;

    @Column(nullable = false)
    private String bookPublisher;

    @Column(nullable = false)
    private LocalDate bookPublished;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private BookCategory category;

    @OneToMany(mappedBy = "book")
    private List<BookCopy> copyList = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<Review> reviewList = new ArrayList<>();
}
