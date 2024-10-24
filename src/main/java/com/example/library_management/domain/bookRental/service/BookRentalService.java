package com.example.library_management.domain.bookRental.service;

import com.example.library_management.domain.book.exception.AuthorizedAdminException;
import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.example.library_management.domain.bookCopy.exception.FindBookCopyException;
import com.example.library_management.domain.bookCopy.repository.BookCopyRepository;
import com.example.library_management.domain.bookRental.dto.BookRentalRequestDto;
import com.example.library_management.domain.bookRental.dto.BookRentalResponseDto;
import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.bookRental.exception.RentableException;
import com.example.library_management.domain.bookRental.repository.BookRentalRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.domain.user.repository.UserRepository;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookRentalService {
    private final BookRentalRepository bookRentalRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;

    public Boolean validateUser(UserDetailsImpl userDetails) {
        return userDetails.getUser().getRole().equals(UserRole.ROLE_ADMIN);
    }

    public BookRentalResponseDto submitBookRental(BookRentalRequestDto bookRentalRequestDto, UserDetailsImpl userDetails) {
        if(!validateUser(userDetails)) {
            throw new AuthorizedAdminException();
        }

        BookCopy bookCopy = bookCopyRepository.findById(bookRentalRequestDto.getBookCopyId()).orElseThrow(FindBookCopyException::new);

        if(!bookCopy.isRentable()) {
            throw new RentableException();
        }

        User user = userRepository.findById(bookRentalRequestDto.getUserId()).orElseThrow(NotFoundUserException::new);

        // 대여하려는 유저의 상태 체크 ex) 연체 패널티 상태이거나 대여가능 권수를 초과한 경우

        BookRental bookRental = new BookRental(bookCopy, user, bookRentalRequestDto.getRentalDate());

        BookRental savedBookRental = bookRentalRepository.save(bookRental);

        bookCopy.updateBookCopy(null, null, null, false);

        BookCopy rentaledBookCopy = bookCopyRepository.save(bookCopy);

        return new BookRentalResponseDto(savedBookRental);
    }
}
