package com.example.library_management.domain.book.entity;

import com.example.library_management.domain.book.cache.bookAuthor.entity.BookAuthor;
import com.example.library_management.domain.book.cache.bookPublisher.entity.BookPublisher;
import com.example.library_management.domain.book.cache.bookSubject.entity.BookSubject;
import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.example.library_management.domain.review.entity.Review;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String bookTitle;

    private Long bookPublished;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_author_mapping",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private Set<BookAuthor> authors = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinTable(
            name = "book_publisher_mapping",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "publisher_id")
    )
    private Set<BookPublisher> publishers = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JsonManagedReference
    @JoinTable(
            name = "book_subject_mapping",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    private Set<BookSubject> subjects = new HashSet<>();

    @OneToMany(mappedBy = "book")
    private List<BookCopy> copyList = new ArrayList<>();

    @OneToMany(mappedBy = "book")
    private List<Review> reviewList = new ArrayList<>();

    public Book(String isbn, String bookTitle, Long bookPublished) {
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.bookPublished = bookPublished;
    }

    public void update(
            String isbn,
            String bookTitle,
            Long bookPublished
    ) {
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.bookPublished = bookPublished;
    }

    public void addAuthor(BookAuthor bookAuthor) {
        authors.add(bookAuthor);
    }

    public void addPublisher(BookPublisher bookPublisher) {
        publishers.add(bookPublisher);
    }

    public void addSubject(BookSubject bookSubject) {
        subjects.add(bookSubject);
    }

    public void addAuthors(Set<BookAuthor> bookAuthors) {
        authors.addAll(bookAuthors);
    }

    public void addPublishers(Set<BookPublisher> bookPublishers) {
        publishers.addAll(bookPublishers);
    }

    public void addSubjects(Set<BookSubject> bookSubjects) {
        subjects.addAll(bookSubjects);
    }


}
