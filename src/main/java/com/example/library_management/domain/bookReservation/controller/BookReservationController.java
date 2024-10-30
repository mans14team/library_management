package com.example.library_management.domain.bookReservation.controller;

import com.example.library_management.domain.bookReservation.entity.BookReservation;
import com.example.library_management.domain.bookReservation.repository.BookReservationRepository;
import com.example.library_management.domain.bookReservation.service.BookReservationService;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookReservation")
public class BookReservationController {
    private final BookReservationService bookReservationService;

    @PostMapping
    public BookReservation submitBookReservation(@RequestBody BookReservation bookReservation, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        return null;
    }
}
