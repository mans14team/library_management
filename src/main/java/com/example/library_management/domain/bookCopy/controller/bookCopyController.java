package com.example.library_management.domain.bookCopy.controller;

import com.example.library_management.domain.bookCopy.dto.BookCopyRequestDto;
import com.example.library_management.domain.bookCopy.dto.BookCopyResponseDto;
import com.example.library_management.domain.bookCopy.service.BookCopyService;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/books/copies")
public class bookCopyController {
    private final BookCopyService bookCopyService;

    @PostMapping
    public ResponseEntity<BookCopyResponseDto> addBookCopy(@RequestBody BookCopyRequestDto bookCopyRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BookCopyResponseDto bookCopyResponseDto = bookCopyService.addBookCopy(bookCopyRequestDto, userDetails);
        return ResponseEntity.ok(bookCopyResponseDto);
    }

    @PutMapping("/{book_copy_id}")
    public ResponseEntity<BookCopyResponseDto> updateBookCopy(@RequestBody BookCopyRequestDto bookCopyRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable("book_copy_id") Long bookCopyId) {
        BookCopyResponseDto bookCopyResponseDto = bookCopyService.updateBookCopy(bookCopyRequestDto, userDetails, bookCopyId);
        return ResponseEntity.ok(bookCopyResponseDto);
    }

    @DeleteMapping("/{book_copy_id}")
    public ResponseEntity<Long> deleteBookCopy(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable("book_copy_id") Long bookCopyId) {
        Long deletedBookCopyId = bookCopyService.deleteBookCopy(userDetails, bookCopyId);
        return ResponseEntity.ok(deletedBookCopyId);
    }

    @GetMapping("/{book_copy_id}")
    public ResponseEntity<BookCopyResponseDto> getBookCopyById(@PathVariable("book_copy_id") Long bookCopyId) {
        BookCopyResponseDto bookCopyResponseDto = bookCopyService.getBookCopyById(bookCopyId);
        return ResponseEntity.ok(bookCopyResponseDto);
    }
}
