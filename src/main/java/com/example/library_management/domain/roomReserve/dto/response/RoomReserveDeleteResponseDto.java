package com.example.library_management.domain.roomReserve.dto.response;

import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RoomReserveDeleteResponseDto {
    private final Long id;

    private final Long userId;

    private final LocalDateTime reservationDate;    //예약 시작일

    private final LocalDateTime reservationDateEnd; //예약 종료일

    // Timestamp로 정의한 생성일과, 수정일
    private final LocalDateTime createdAt;

    private final LocalDateTime modifiedAt;

    public RoomReserveDeleteResponseDto(RoomReserve roomReserve) {
        this.id = roomReserve.getId();
        this.userId = roomReserve.getUser().getId();
        this.reservationDate = roomReserve.getReservationDate();
        this.reservationDateEnd = roomReserve.getReservationDateEnd();
        this.createdAt = roomReserve.getCreatedAt();
        this.modifiedAt = roomReserve.getModifiedAt();
    }
}
