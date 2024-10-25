package com.example.library_management.domain.bookRental.repository;

import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.bookRental.enums.RentalState;
import com.example.library_management.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookRentalRepository extends JpaRepository<BookRental, Long> {
    Optional<BookRental> findByBookCopyAndRentalState(BookCopy bookCopy, RentalState rentalState);

    List<BookRental> findAllByUser(User user);


@Query("select br from BookRental where")
    List<BookRental> findAllRentalDate(@Param("targetDate") LocalDate targetDate);
}
