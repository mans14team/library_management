package com.example.library_management.domain.bookCopy.service;

import com.example.library_management.domain.bookCopy.repository.BookCopyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookCopyService {
    private final BookCopyRepository bookCopyRepository;
}
