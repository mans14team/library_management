package com.example.library_management.domain.bookCopy.entity;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.bookReservation.entity.BookReservation;
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
@Table(name = "book_copy")
@NoArgsConstructor
public class BookCopy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private LocalDate registeredAt;

    private LocalDate discardedAt;

    private boolean rentable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @OneToMany(mappedBy = "bookCopy")
    private List<BookRental> rentalList = new ArrayList<>();

    @OneToMany(mappedBy = "bookCopy")
    private List<BookReservation> reservationList = new ArrayList<>();

    public BookCopy(Book book, LocalDate registeredAt) {
        this.book = book;
        this.registeredAt = registeredAt;
        this.rentable = true;
    }

    public void updateBookCopy(Book book, LocalDate registeredAt, LocalDate discardedAt, boolean rentable) {
        if(book != null) this.book = book;
        if(registeredAt != null) this.registeredAt = registeredAt;
        if(discardedAt != null) this.discardedAt = discardedAt;
        this.rentable = rentable;
    }
}
