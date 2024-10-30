package com.example.library_management.domain.bookRental.service;

import com.example.library_management.domain.book.exception.AuthorizedAdminException;
import com.example.library_management.domain.bookCopy.entity.BookCopy;
import com.example.library_management.domain.bookCopy.exception.FindBookCopyException;
import com.example.library_management.domain.bookCopy.repository.BookCopyRepository;
import com.example.library_management.domain.bookRental.dto.BookRentalRequestDto;
import com.example.library_management.domain.bookRental.dto.BookRentalResponseDto;
import com.example.library_management.domain.bookRental.entity.BookRental;
import com.example.library_management.domain.bookRental.enums.RentalState;
import com.example.library_management.domain.bookRental.exception.AlreadyReturnException;
import com.example.library_management.domain.bookRental.exception.DiffrentBookCopyReservationException;
import com.example.library_management.domain.bookRental.exception.NotFoundBookRenalHistoryException;
import com.example.library_management.domain.bookRental.exception.RentableException;
import com.example.library_management.domain.bookRental.repository.BookRentalRepository;
import com.example.library_management.domain.bookReservation.entity.BookReservation;
import com.example.library_management.domain.bookReservation.entity.ReservatationState;
import com.example.library_management.domain.bookReservation.repository.BookReservationRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import com.example.library_management.domain.user.exception.NotFoundUserException;
import com.example.library_management.domain.user.repository.UserRepository;
import com.example.library_management.global.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookRentalService {
    private final BookRentalRepository bookRentalRepository;
    private final BookCopyRepository bookCopyRepository;
    private final UserRepository userRepository;
    private final BookReservationRepository bookReservationRepository;

    public Boolean validateUser(UserDetailsImpl userDetails) {
        return userDetails.getUser().getRole().equals(UserRole.ROLE_ADMIN);
    }

    @Transactional
    public BookRentalResponseDto submitBookRental(BookRentalRequestDto bookRentalRequestDto, UserDetailsImpl userDetails) throws DiffrentBookCopyReservationException {
        if(!validateUser(userDetails)) {
            throw new AuthorizedAdminException();
        }

        User user = userRepository.findById(bookRentalRequestDto.getUserId()).orElseThrow(NotFoundUserException::new);

        BookCopy bookCopy = bookCopyRepository.findById(bookRentalRequestDto.getBookCopyId()).orElseThrow(FindBookCopyException::new);

        // 빌리려는 유저의 대여 예약 목록 조회(현재 빌리려는 책과 같은 종류의 도서만)
        Optional<BookReservation> bookReservationOpt = bookReservationRepository
                .findAllByUser(user)
                .stream()
                .filter(br -> br.getUser().equals(user) && br.getBookCopy().getBook().equals(bookCopy.getBook()) && br.getState().equals(ReservatationState.ACTIVE))
                .findFirst();

        // 진행중인 예약 내역이 없을 경우
        if(!bookReservationOpt.isPresent()) {
            // 예약 내역이 존재하지 않을 때, 빌리려는 서적이 대여 불가인 경우 예외처리
            if(!bookCopy.isRentable()) {
                throw new RentableException();
            }
        }
        // 현재 빌리려는 도서에 대한 진행중인 예약 내역이 존재할 경우
        else {
            BookReservation bookReservation = bookReservationOpt.get();

            // 같은 도서의 대여 예약 내역이 존재하는데, 복본 ID가 다른 경우 예외처리(예약된 복본 ID 출력)
            if(!bookReservation.getBookCopy().equals(bookCopy)) {
                BookCopy reservedBookCopy = bookCopyRepository.findById(bookReservation.getBookCopy().getId()).orElseThrow(FindBookCopyException::new);
                throw new DiffrentBookCopyReservationException(reservedBookCopy.getId());
            }

            // 빌리려는 책(복본)이 유저의 진행중인 대여 예약 내역에 존재할 경우, 해당 대여 예약의 상태를 '종료'로 바꾸고 저장.
            bookReservation.finishReservation();
            bookReservationRepository.save(bookReservation);
        }

        // 대여하려는 유저의 상태 체크 추가 필요 ex) 연체 패널티 상태이거나 대여가능 권수를 초과한 경우

        LocalDateTime rentDate = LocalDateTime.now();

        BookRental bookRental = new BookRental(bookCopy, user, rentDate);

        BookRental savedBookRental = bookRentalRepository.save(bookRental);

        bookCopy.updateBookCopy(null, null, null, false);

        BookCopy rentaledBookCopy = bookCopyRepository.save(bookCopy);

        return new BookRentalResponseDto(savedBookRental);
    }

    @Transactional
    public BookRentalResponseDto returnBookRental(BookRentalRequestDto bookRentalRequestDto, UserDetailsImpl userDetails) {
        if(!validateUser(userDetails)) {
            throw new AuthorizedAdminException();
        }

        BookCopy bookCopy = bookCopyRepository.findById(bookRentalRequestDto.getBookCopyId()).orElseThrow(FindBookCopyException::new);

        if(bookCopy.isRentable()) {
            throw new AlreadyReturnException();
        }

        BookRental bookRental = bookRentalRepository.findByBookCopyAndRentalState(bookCopy, RentalState.ACTIVE).orElseThrow(NotFoundBookRenalHistoryException::new);

        bookRental.updateBookRental();

        bookCopy.updateBookCopy(null, null, null, true);
        bookCopyRepository.save(bookCopy);

        BookRental returnedBookRental = bookRentalRepository.save(bookRental);

        return new BookRentalResponseDto(returnedBookRental);
    }

    @Transactional
    public Long deleteBookRental(Long bookRentalId, UserDetailsImpl userDetails) {
        if(!validateUser(userDetails)) {
            throw new AuthorizedAdminException();
        }

        BookRental bookRental = bookRentalRepository.findById(bookRentalId).orElseThrow(NotFoundBookRenalHistoryException::new);

        bookRentalRepository.delete(bookRental);

        BookCopy bookCopy = bookRental.getBookCopy();
        bookCopy.updateBookCopy(null, null, null, true);
        bookCopyRepository.save(bookCopy);

        return bookRental.getId();
    }

    // 로그인한 유저의 대여 기록
    public List<BookRentalResponseDto> getBookRental(UserDetailsImpl userDetails) {
        List<BookRental> bookRentalHistory = bookRentalRepository.findAllByUser(userDetails.getUser());

        return bookRentalHistory.stream().map(BookRentalResponseDto::new).toList();
    }

    // 관리자만 조회 가능한 '모든 유저의 대여기록 조회'
    public List<BookRentalResponseDto> getAllBookRental(UserDetailsImpl userDetails) {
        if(!validateUser(userDetails)) {
            throw new AuthorizedAdminException();
        }

        List<BookRental> bookRentalHistory = bookRentalRepository.findAll();
        return bookRentalHistory.stream().map(BookRentalResponseDto::new).toList();
    }
}
