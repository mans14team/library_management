package com.example.library_management.book;

import com.example.library_management.domain.book.dto.BookRequestDto;
import com.example.library_management.domain.book.dto.BookResponseDto;
import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.exception.AuthorizedAdminException;
import com.example.library_management.domain.book.exception.FindBookException;
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
import static org.mockito.Mockito.*;

public class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookCategoryRepository categoryRepository;

    @Mock
    private UserDetailsImpl userDetails; // Mock 객체로 정의

    @Mock
    private BookCategory bookCategory;

    private BookRequestDto bookRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 테스트에 사용할 샘플 데이터 설정
        bookRequestDto = new BookRequestDto();
        bookRequestDto.setBookTitle("테스트 도서");
        bookRequestDto.setBookDescription("도서 설명");
        bookRequestDto.setBookAuthor("저자 이름");
        bookRequestDto.setBookPublisher("출판사 이름");
        bookRequestDto.setBookPublished(LocalDate.of(2024, 1, 1)); // LocalDate 설정
        bookRequestDto.setCategoryId(1L);

        bookCategory = new BookCategory();
        bookCategory.setId(1L); // 카테고리 ID 설정
        bookCategory.setCategoryName("문학");


        // User 객체 설정
        User user = new User();
        user.setRole(UserRole.ROLE_ADMIN); // 관리자 역할 설정

        // UserDetailsImpl을 mock으로 설정
        when(userDetails.getUser()).thenReturn(user);
    }

    @Test
    void testAddBook() {
        // Given
        when(categoryRepository.findById(any(Long.class))).thenReturn(Optional.of(bookCategory));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BookResponseDto responseDto = bookService.addBook(bookRequestDto, userDetails); // userDetails 전달

        // Then
        assertNotNull(responseDto);
        assertEquals("테스트 도서", responseDto.getBookTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testUpdateBook() {
        // Given
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setBookTitle("기존 도서 제목");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BookResponseDto responseDto = bookService.updateBook(1L, bookRequestDto, userDetails); // userDetails 전달

        // Then
        assertNotNull(responseDto);
        assertEquals("테스트 도서", responseDto.getBookTitle());
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
        User user = new User();
        user.setRole(UserRole.ROLE_USER); // 일반 사용자 설정
        when(userDetails.getUser()).thenReturn(user); // 일반 사용자 역할 Mock 설정

        // When & Then
        assertThrows(AuthorizedAdminException.class, () -> bookService.addBook(bookRequestDto, userDetails));
    }

    @Test
    void testGetBookById() {
        // Given
        Book existingBook = new Book();
        existingBook.setId(1L);
        existingBook.setBookTitle("도서 제목");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));

        // When
        BookResponseDto responseDto = bookService.getBookById(1L);

        // Then
        assertNotNull(responseDto);
        assertEquals("도서 제목", responseDto.getBookTitle());
    }

    @Test
    void testGetBookByIdNotFound() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(FindBookException.class, () -> bookService.getBookById(1L));
    }
}
