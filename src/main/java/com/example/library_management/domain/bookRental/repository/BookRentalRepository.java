package com.example.library_management.domain.bookRental.repository;

import com.example.library_management.domain.bookRental.entity.BookRental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRentalRepository extends JpaRepository<BookRental, Long> {
}
