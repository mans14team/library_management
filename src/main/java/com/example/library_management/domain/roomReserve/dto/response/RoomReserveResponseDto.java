package com.example.library_management.domain.roomReserve.dto.response;

import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RoomReserveResponseDto {
    private Long id;

    private Long userId;

    private LocalDateTime reservationDate;    //예약 시작일

    private LocalDateTime reservationDateEnd; //예약 종료일

    // Timestamp로 정의한 생성일과, 수정일
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public RoomReserveResponseDto(RoomReserve roomReserve) {
        this.id = roomReserve.getId();
        this.userId = roomReserve.getUser().getId();
        this.reservationDate = roomReserve.getReservationDate();
        this.reservationDateEnd = roomReserve.getReservationDateEnd();
        this.createdAt = roomReserve.getCreatedAt();
        this.modifiedAt = roomReserve.getModifiedAt();
    }
}
