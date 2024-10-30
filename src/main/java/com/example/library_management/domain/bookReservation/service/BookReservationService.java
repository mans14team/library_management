package com.example.library_management.domain.bookReservation.service;

import com.example.library_management.domain.book.entity.Book;
import com.example.library_management.domain.book.exception.AuthorizedAdminException;
import com.example.library_management.domain.book.exception.FindBookException;
import com.example.library_management.domain.book.repository.BookRepository;
import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.example.library_management.domain.bookCopy.exception.FindBookCopyException;
import com.example.library_management.domain.bookCopy.repository.BookCopyRepository;
import com.example.library_management.domain.bookReservation.dto.BookReservationRequestDto;
import com.example.library_management.domain.bookReservation.dto.BookReservationResponseDto;
import com.example.library_management.domain.bookReservation.entity.BookReservation;
import com.example.library_management.domain.bookReservation.exception.FindBookReservationException;
import com.example.library_management.domain.bookReservation.exception.NotRentableBookException;
import com.example.library_management.domain.bookReservation.repository.BookReservationRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.global.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class BookReservationService {
    private final BookReservationRepository bookReservationRepository;
    private final BookRepository bookRepository;
    private final BookCopyRepository bookCopyRepository;


    public BookReservationResponseDto submitBookReservation(BookReservationRequestDto bookReservationRequestDto, UserDetailsImpl userDetails) {
        // + 유저 멤버쉽 확인
        // + 유저 검증 -> 대여 가능한 상태인지? 아래 유저 검증이랑 합치기
        User user = userDetails.getUser();
        if(user == null) {
            throw new NotFoundUserException();
        }

        Book book = bookRepository.findById(bookReservationRequestDto.getBookId()).orElseThrow(FindBookException::new);
        Optional<BookCopy> bookCopyOpt = bookCopyRepository.findAllByBookAndRentableTrue(book).stream().findFirst();

        if(!bookCopyOpt.isPresent()) {
            throw new NotRentableBookException();
        }

        BookCopy bookCopy = bookCopyOpt.get();
        bookCopy.setRentable(false);

        BookReservation bookReservation = new BookReservation(LocalDate.now(), bookCopy, user);

        bookCopyRepository.save(bookCopy);

        BookReservation savedBookReservation = bookReservationRepository.save(bookReservation);

        return new BookReservationResponseDto(savedBookReservation);
    }

    // 기한 만료시 자동으로 호출될 로직
    public Long deleteBookReservation(Long bookReservationId, UserDetailsImpl userDetails) {
        if(!validateUserAdmin(userDetails)){
            throw new AuthorizedAdminException();
        }

        BookReservation bookReservation = bookReservationRepository.findById(bookReservationId).orElseThrow(FindBookReservationException::new);

        BookCopy bookCopy = bookCopyRepository.findById(bookReservation.getBookCopy().getId()).orElseThrow(FindBookCopyException::new);

        bookCopy.setRentable(true);

        bookCopyRepository.save(bookCopy);
        bookReservationRepository.delete(bookReservation);

        return bookReservationId;
    }

    public Boolean validateUserAdmin(UserDetailsImpl userDetails) {
        return userDetails.getUser().getRole().equals(UserRole.ROLE_ADMIN);
    }

    public List<BookReservationResponseDto> getBookReservations(UserDetailsImpl userDetails) {
        if(!validateUserAdmin(userDetails)){
            throw new AuthorizedAdminException();
        }

        List<BookReservation> bookReservationList = bookReservationRepository.findAll();

        return bookReservationList.stream().map(BookReservationResponseDto::new).toList();
    }

    public List<BookReservationResponseDto> getBookReservationsByUser(UserDetailsImpl userDetails) {
        User user = userDetails.getUser();

        List<BookReservation> bookReservationList = bookReservationRepository.findAllByUser(user);
        return bookReservationList.stream().map(BookReservationResponseDto::new).toList();
    }

    // 예약일 + 3일째에 대여 안하면 예약 취소
}
