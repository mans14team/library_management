package com.example.library_management.domain.bookReservation.entity;

import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.example.library_management.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservatationState state;

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
        this.state = ReservatationState.ACTIVE;
    }

    public void expireReservation() {
        this.state = ReservatationState.EXPIRED;
    }

    public void finishReservation() {
        this.state = ReservatationState.FINISHED;
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.of(23, 50, 0); // 12시 직전
        LocalTime end = LocalTime.of(0, 9, 59); // 12시 직후

        if (now.isAfter(start) && now.isBefore(end)) {
            throw new IllegalStateException("23:50 ~ 00:10 동안에는 예약 상태를 변경할 수 없습니다.");
        }
    }
}
