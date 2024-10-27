package com.example.library_management.domain.bookReservation.repository;

import com.example.library_management.domain.bookReservation.entity.BookReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface BookReservationRepository extends JpaRepository<BookReservation, Long> {
}
