package com.example.library_management.domain.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalExceptionConst {
    // 상태코드 400
    DUPLICATE_PASSWORD(HttpStatus.BAD_REQUEST, " 새 비밀번호는 이전에 사용한 비밀번호와 같을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, " 새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다."),
    INVALID_REVIEWSTAR(HttpStatus.BAD_REQUEST, " 리뷰 별점은 1~5점이여야합니다."),
    INSUFFICIENT_DATA_DELIVERED(HttpStatus.BAD_REQUEST, "예약 시작일과 종료일 중 하나는 필수로 입력되어야합니다."),
    MISMATCHED_COMMENT_BOARD(HttpStatus.BAD_REQUEST, "해당 게시글의 댓글이 아닙니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액이 유효하지 않습니다."),
    INVALID_PAYMENT_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 결제 요청입니다."),
    BILLING_KEY_REGISTRATION_ERROR(HttpStatus.BAD_REQUEST, "잘못된 빌링키 발급 요청입니다."),

    // 상태코드 401
    UNAUTHORIZED_PASSWORD(HttpStatus.UNAUTHORIZED, " 비밀번호를 확인해주세요."),
    UNAUTHORIZED_OWNERTOKEN(HttpStatus.UNAUTHORIZED, " 유저 토큰이 틀렸습니다."),
    UNAUTHORIZED_ADMIN(HttpStatus.UNAUTHORIZED, " 관리자 권한이 존재하지 않습니다."),
    UNAUTHORIZED_PAYMENT(HttpStatus.UNAUTHORIZED, "결제 인증에 실패했습니다."),

    // 상태코드 403
    FORBIDDEN_CREATE(HttpStatus.FORBIDDEN, " 게시글 권한이 없습니다."),
    FORBIDDEN_CREATE_REVIEW(HttpStatus.FORBIDDEN, " 리뷰를 수정/삭제할 권한이 없습니다."),
    FORBIDDEN_COMMENT_ACCESS(HttpStatus.FORBIDDEN, " 댓글 접근에 권한이 없습니다."),
    FORBIDDEN_ROOMCONTROL(HttpStatus.FORBIDDEN, " 스터디룸 관리 권한이 없습니다."),
    FORBIDDEN_RESERVATION_MODIFICATION(HttpStatus.FORBIDDEN, "본인의 스터디룸 예약만 수정할 수 있습니다."),
    FORBIDDEN_RESERVATION_DELETE(HttpStatus.FORBIDDEN, "본인의 스터디룸 예약만 삭제할 수 있습니다."),

    // 상태코드 404
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, " 회원이 존재하지 않습니다."),
    NOT_FOUND_EMAIL(HttpStatus.NOT_FOUND, " 이메일을 확인해주세요."),
    DELETED_USER(HttpStatus.NOT_FOUND, " 탈퇴된 회원입니다."),
    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, " 게시글이 존재하지 않습니다."),
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, " 리뷰가 존재하지 않습니다."),
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, " 존재하지 않는 카테고리입니다."),
    NOT_FOUND_BOOK(HttpStatus.NOT_FOUND, " 존재하지 않는 도서 ID 입니다."),
    NOT_FOUND_BOOK_COPY(HttpStatus.NOT_FOUND, " 존재하지 않는 복본 ID 입니다."),
    NOT_FOUND_BOOK_RENTAL(HttpStatus.NOT_FOUND, " 존재하지 않는 대여기록입니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, " 댓글을 찾을 수 없습니다."),
    NOT_FOUND_ROOM(HttpStatus.NOT_FOUND, " 스터디룸이 존재하지 않습니다."),
    NOT_FOUND_ROOM_RESERVE(HttpStatus.NOT_FOUND, " 스터디룸 예약이 존재하지 않습니다."),
    NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, "결제 내역을 찾을 수 없습니다."),
    NOT_FOUND_MEMBERSHIP(HttpStatus.NOT_FOUND, "멤버십 정보를 찾을 수 없습니다."),
    NO_ACTIVE_MEMBERSHIP(HttpStatus.NOT_FOUND, "활성화된 멤버십을 찾을 수 없습니다."),
    NOT_FOUND_RENTABLE_BOOKCOPY(HttpStatus.NOT_FOUND, "대여 가능한 서적이 존재하지 않습니다."),
    NOT_FOUND_BOOK_RESERVATION(HttpStatus.NOT_FOUND, "존재하지 않는 책 대여 예약 정보입니다."),

    // 상태코드 409
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, " 중복된 이메일입니다."),
    RENTAL_NOT_POSSIBLE(HttpStatus.CONFLICT, " 현재 대여 불가능한 서적입니다."),
    ALREADY_RETURN(HttpStatus.CONFLICT, " 이미 반납된 서적입니다."),
    ROOM_RESERVE_OVERLAP(HttpStatus.CONFLICT, "예약 시간이 겹쳐 예약이 불가능합니다."),
    DUPLICATE_PAYMENT(HttpStatus.CONFLICT, "이미 처리된 결제입니다."),
    ACTIVE_MEMBERSHIP_EXISTS(HttpStatus.CONFLICT, "이미 활성화된 멤버십이 존재합니다."),

    // 상태코드 422
    ROOM_RESERVE_UNPROCESSABLE(HttpStatus.UNPROCESSABLE_ENTITY, "해당 스터디룸 에약이 불가능합니다."),

    // 상태코드 500
    PAYMENT_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "결제 서버 오류가 발생했습니다."),
    PAYMENT_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
