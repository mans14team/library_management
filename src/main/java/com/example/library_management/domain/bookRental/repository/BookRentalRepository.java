package com.example.library_management.domain.bookRental.repository;

import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.bookRental.enums.RentalState;
import com.example.library_management.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRentalRepository extends JpaRepository<BookRental, Long> {
    Optional<BookRental> findByBookCopyAndRentalState(BookCopy bookCopy, RentalState rentalState);

    List<BookRental> findAllByUser(User user);
}
