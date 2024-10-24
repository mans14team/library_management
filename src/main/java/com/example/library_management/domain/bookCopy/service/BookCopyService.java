package com.example.library_management.domain.bookCopy.service;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.exception.AuthorizedAdminException;
import com.example.library_management.domain.book.exception.FindBookException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.bookCopy.dto.BookCopyRequestDto;
import com.example.library_management.domain.bookCopy.dto.BookCopyResponseDto;
import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.example.library_management.domain.bookCopy.exception.FindBookCopyException;
import com.example.library_management.domain.bookCopy.repository.BookCopyRepository;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookCopyService {
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public BookCopyResponseDto addBookCopy(BookCopyRequestDto bookCopyRequestDto, UserDetailsImpl userDetails) {
        if(!validateUserAdmin(userDetails)){
            throw new AuthorizedAdminException();
        }

        Book book = bookRepository.findById(bookCopyRequestDto.getBookId()).orElseThrow(FindBookException::new);
        BookCopy bookCopy = new BookCopy(book, bookCopyRequestDto.getRegisteredAt());

        BookCopy savedBookCopy = bookCopyRepository.save(bookCopy);

        return new BookCopyResponseDto(savedBookCopy.getId(), savedBookCopy.getBook().getBookTitle());
    }

    public BookCopyResponseDto updateBookCopy(BookCopyRequestDto bookCopyRequestDto, UserDetailsImpl userDetails, Long bookCopyId) {
        if(!validateUserAdmin(userDetails)){
            throw new AuthorizedAdminException();
        }

        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId).orElseThrow(FindBookException::new);

        Book book = bookRepository.findById(bookCopyRequestDto.getBookId()).orElseThrow(FindBookException::new);

        bookCopy.updateBookCopy(book, bookCopyRequestDto.getRegisteredAt(), null, bookCopy.isRentable());

        BookCopy updatedBookCopy = bookCopyRepository.save(bookCopy);

        return new BookCopyResponseDto(updatedBookCopy.getId(), updatedBookCopy.getBook().getBookTitle());
    }

    public Boolean validateUserAdmin(UserDetailsImpl userDetails) {
        return userDetails.getUser().getRole().equals(UserRole.ROLE_ADMIN);
    }

    public Long deleteBookCopy(UserDetailsImpl userDetails, Long bookCopyId) {
        if(!validateUserAdmin(userDetails)){
            throw new AuthorizedAdminException();
        }

        BookCopy bookCopy = bookCopyRepository.findById(bookCopyId).orElseThrow(FindBookCopyException::new);

        bookCopyRepository.delete(bookCopy);

        return bookCopyId;
    }
}
