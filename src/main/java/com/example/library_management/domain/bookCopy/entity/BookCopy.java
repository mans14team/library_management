package com.example.library_management.domain.bookCopy.entity;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.bookReservation.entity.BookReservation;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "book_copy")
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime registeredAt;

    private LocalDateTime discardedAt;
    private boolean rentable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @OneToMany(mappedBy = "bookCopy")
    private List<BookRental> rentalList = new ArrayList<>();

    @OneToMany(mappedBy = "bookCopy")
    private List<BookReservation> reservationList = new ArrayList<>();
}
