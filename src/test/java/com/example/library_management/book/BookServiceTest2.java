package com.example.library_management.book;

import com.example.library_management.domain.book.dto.BookRequestDto;
import com.example.library_management.domain.book.dto.BookResponseDto;
import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.exception.AuthorizedAdminException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.book.service.BookService;
import com.example.library_management.domain.bookCategory.entity.BookCategory;
import com.example.library_management.domain.bookCategory.repository.BookCategoryRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.global.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BookServiceTest2 {
    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    private UserDetailsImpl userDetails;

    private UserDetailsImpl userDetails2;

    private User user;
    private User user2;

    private Book book;

    private BookCategory bookCategory1;
    private BookCategory bookCategory2;

    private BookRequestDto bookRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.
                initMocks(this);

        bookCategory1 = new BookCategory();
        bookCategory1.setId(1L);
        bookCategory1.setCategoryName("문학");

        bookCategory2 = new BookCategory();
        bookCategory2.setId(2L);
        bookCategory2.setCategoryName("과학");

        book = new Book();
        book.setId(1L);
        book.setBookTitle("아프리카 청춘");
        book.setCategory(bookCategory1);

        bookRequestDto = new BookRequestDto();
        bookRequestDto.setBookTitle("개미");
        bookRequestDto.setBookAuthor("베르나르 베르베르");
        bookRequestDto.setBookDescription("소설");
        bookRequestDto.setBookPublisher("문학사");
        bookRequestDto.setBookPublished(LocalDate.parse("2012-08-12"));
        bookRequestDto.setCategoryId(1L);

        user = new User();
        user.setRole(UserRole.ROLE_ADMIN);
        userDetails = new UserDetailsImpl(user);

        user2 = new User();
        user2.setRole(UserRole.ROLE_USER);
        userDetails2 = new UserDetailsImpl(user2);

    }

    @Test
    void testAddBook() {
        // Given
        when(bookCategoryRepository.findById(any(Long.class))).thenReturn(Optional.of(bookCategory1));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BookResponseDto responseDto = bookService.addBook(bookRequestDto, userDetails); // userDetails 전달

        // Then
        assertNotNull(responseDto);
        assertEquals("개미", responseDto.getBookTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testUpdateBook() {
        // Given
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setBookTitle("기존 도서 제목");
        existingBook.setCategory(bookCategory2);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bookCategoryRepository.findById(1L)).thenReturn(Optional.of(bookCategory1));

        // When
        BookResponseDto responseDto = bookService.updateBook(1L, bookRequestDto, userDetails); // userDetails 전달

        // Then
        assertNotNull(responseDto);
        assertEquals("개미", responseDto.getBookTitle());
        assertEquals("문학", responseDto.getCategory());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testDeleteBook() {
        // Given
        Book existingBook = new Book();
        existingBook.setId(1L);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));

        // When
        Long deletedBookId = bookService.deleteBook(1L, userDetails); // userDetails 전달

        // Then
        assertEquals(1L, deletedBookId);
        verify(bookRepository).delete(existingBook);
    }

    @Test
    void testAddBookUnauthorized() {
        // Given


        // When & Then
        assertThrows(AuthorizedAdminException.class, () -> bookService.addBook(bookRequestDto, userDetails2));
    }


}
