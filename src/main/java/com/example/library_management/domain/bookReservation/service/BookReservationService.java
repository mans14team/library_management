package com.example.library_management.domain.bookReservation.service;

import com.example.library_management.domain.bookReservation.repository.BookReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class BookReservationService {
    private final BookReservationRepository bookReservationRepository;

}
