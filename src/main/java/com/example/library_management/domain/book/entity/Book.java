package com.example.library_management.domain.book.entity;

import com.example.library_management.domain.book.dto.BookRequestDto;
import com.example.library_management.domain.bookCategory.entity.BookCategory;
import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.example.library_management.domain.review.entity.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bookTitle;



//    @ElementCollection
//    private List<String> bookAuthor;
//
//    @ElementCollection
//    private List<String> bookPublisher;
//
//    @ElementCollection
//    private List<Long> bookPublished;
//
//    @ElementCollection
//    private List<Long> bookSubject;



    //========== 지울거=========
    private String bookDescription;

    private String bookAuthor;

    @Column(nullable = false)
    private String bookPublisher;

    @Column(nullable = false)
    private LocalDate bookPublished;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private BookCategory category;
    //=========================





    @OneToMany(mappedBy = "book")
    private List<BookCopy> copyList = new ArrayList<>();

    @OneToMany(mappedBy = "book",cascade = CascadeType.REMOVE)
    private List<Review> reviewList = new ArrayList<>();

    public Book(String bookTitle, String bookDescription, String bookAuthor, String bookPublisher, LocalDate bookPublished, BookCategory category) {
        this.bookTitle = bookTitle;
        this.bookDescription = bookDescription;
        this.bookAuthor = bookAuthor;
        this.bookPublisher = bookPublisher;
        this.bookPublished = bookPublished;
        this.category = category;
    }

    public void update(String bookTitle, String bookDescription, String bookAuthor, String bookPublisher, LocalDate bookPublished, BookCategory category) {
        if(bookTitle != null) this.bookTitle = bookTitle;
        if(bookDescription != null) this.bookDescription = bookDescription;
        if(bookAuthor != null) this.bookAuthor = bookAuthor;
        if(bookPublisher != null) this.bookPublisher = bookPublisher;
        if(bookPublished != null) this.bookPublished = bookPublished;
        this.category = category;
    }
}
