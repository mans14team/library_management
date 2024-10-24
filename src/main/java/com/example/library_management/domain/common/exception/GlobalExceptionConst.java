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
    INSUFFICIENT_DATA_DELIVERED(HttpStatus.BAD_REQUEST, "예약 시작일과 종료일 중 하나는 필수로 입력되어야합니다."),

    // 상태코드 401
    UNAUTHORIZED_PASSWORD(HttpStatus.UNAUTHORIZED, " 비밀번호를 확인해주세요."),
    UNAUTHORIZED_OWNERTOKEN(HttpStatus.UNAUTHORIZED, " 유저 토큰이 틀렸습니다."),
    UNAUTHORIZED_CREATE(HttpStatus.UNAUTHORIZED, " 게시글 권한이 없습니다."),

    UNAUTHORIZED_ROOMCONTROL(HttpStatus.UNAUTHORIZED, " 스터디룸 관리 권한이 없습니다."),
    UNAUTHORIZED_RESERVATION_MODIFICATION(HttpStatus.UNAUTHORIZED, "본인의 스터디룸 예약만 수정할 수 있습니다."),

    // 상태코드 404
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, " 회원이 존재하지 않습니다."),
    NOT_FOUND_EMAIL(HttpStatus.NOT_FOUND, " 이메일을 확인해주세요."),
    DELETED_USER(HttpStatus.NOT_FOUND, " 탈퇴된 회원입니다."),
    NOT_FOUND_BOARD(HttpStatus.NOT_FOUND, " 게시글이 존재하지 않습니다."),

    NOT_FOUND_ROOM(HttpStatus.NOT_FOUND, " 스터디룸이 존재하지 않습니다."),
    NOT_FOUND_ROOM_RESERVE(HttpStatus.NOT_FOUND, " 스터디룸 예약이 존재하지 않습니다."),

    // 상태코드 409
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, " 중복된 이메일입니다."),
    ROOM_RESERVE_OVERLAP(HttpStatus.CONFLICT, "예약 시간이 겹쳐 예약이 불가능합니다."),

    // 상태코드 422
    ROOM_RESERVE_UNPROCESSABLE(HttpStatus.UNPROCESSABLE_ENTITY, "해당 스터디룸 에약이 불가능합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
