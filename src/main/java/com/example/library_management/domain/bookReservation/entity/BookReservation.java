package com.example.library_management.domain.bookReservation.entity;

import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.example.library_management.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Getter
@Table(name = "book_reservation")
@NoArgsConstructor
public class BookReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_copy_id")
    private BookCopy bookCopy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public BookReservation(LocalDate now, BookCopy bookCopy, User user) {
        this.reservationDate = now;
        this.bookCopy = bookCopy;
        this.user = user;
    }
}
