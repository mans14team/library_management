package com.example.library_management.domain.bookRental.service;

import com.example.library_management.domain.bookCopy.repository.BookCopyRepository;
import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.bookRental.repository.BookRentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BookRentalServiceTest {
    @InjectMocks
    private BookRentalService bookRentalService;

    @Mock
    private BookRentalRepository bookRentalRepository;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void submitBookRental() {
        BookRental bookRental = new BookRental();
    }


}
