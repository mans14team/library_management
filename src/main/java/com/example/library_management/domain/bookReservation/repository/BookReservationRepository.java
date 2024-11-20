package com.example.library_management.domain.bookReservation.repository;

import com.example.library_management.domain.bookReservation.entity.BookReservation;
import com.example.library_management.domain.bookReservation.entity.ReservatationState;
import com.example.library_management.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

public interface BookReservationRepository extends JpaRepository<BookReservation, Long> {
    List<BookReservation> findAllByUser(User user);

    List<BookReservation> findByReservationDateBeforeAndState(LocalDate threeDaysAgo, ReservatationState reservatationState);
}
