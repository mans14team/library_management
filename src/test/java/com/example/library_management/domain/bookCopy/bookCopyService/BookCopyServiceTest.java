package com.example.library_management.domain.bookCopy.bookCopyService;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.bookCategory.entity.BookCategory;
import com.example.library_management.domain.bookCopy.dto.BookCopyRequestDto;
import com.example.library_management.domain.bookCopy.dto.BookCopyResponseDto;
import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.example.library_management.domain.bookCopy.repository.BookCopyRepository;
import com.example.library_management.domain.bookCopy.service.BookCopyService;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Optional;

public class BookCopyServiceTest {

    @InjectMocks
    private BookCopyService bookCopyService;

    @Mock
    private BookCopyRepository bookCopyRepository;

    @Mock
    private BookRepository bookRepository;

    private User user;
    private UserDetailsImpl userDetails;

    private Book book;
    private BookCategory bookCategory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.
                initMocks(this);

        user = new User();
        user.setRole(UserRole.ROLE_ADMIN);
        userDetails = new UserDetailsImpl(user);

        bookCategory = new BookCategory();
        bookCategory.setId(1L);
        bookCategory.setCategoryName("문학");

        book = new Book();
        book.setId(1L);
        book.setBookTitle("아프리카 청춘");
        book.setCategory(bookCategory);
    }

    @Test
    void testAddBookCopy() {
        // Arrange
        BookCopy bookCopy = new BookCopy(book, LocalDate.now());
        bookCopy.setId(1L);

        BookCopyRequestDto bookCopyRequestDto = new BookCopyRequestDto();
        bookCopyRequestDto.setBookId(bookCopy.getBook().getId());
        bookCopyRequestDto.setRegisteredAt(bookCopy.getRegisteredAt());

        when(bookRepository.findById(bookCopyRequestDto.getBookId())).thenReturn(Optional.of(book));
        when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(bookCopy);

        // Act
        BookCopyResponseDto bookCopyResponseDto = bookCopyService.addBookCopy(bookCopyRequestDto, userDetails);

        // Assert
        assertNotNull(bookCopyResponseDto);
        verify(bookCopyRepository).save(any(BookCopy.class));
        assertEquals("아프리카 청춘", bookCopyResponseDto.getBookTitle());
    }

    @Test
    void testUpdateBookCopy() {
        BookCopy bookCopy = new BookCopy(book, LocalDate.now());
        bookCopy.setId(1L);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setBookTitle("신");
        book2.setCategory(bookCategory);

        BookCopyRequestDto bookCopyRequestDto = new BookCopyRequestDto();
        bookCopyRequestDto.setBookId(book2.getId());

        when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));
        when(bookCopyRepository.findById(1L)).thenReturn(Optional.of(bookCopy));
        when(bookCopyRepository.save(any(BookCopy.class))).thenReturn(bookCopy);

        BookCopyResponseDto bookCopyResponseDto = bookCopyService.updateBookCopy(bookCopyRequestDto, userDetails, 1L);

        assertNotNull(bookCopyResponseDto);
        verify(bookCopyRepository).save(any(BookCopy.class));
        assertEquals("신", bookCopyResponseDto.getBookTitle());
    }


}
