package com.example.library_management.domain.roomReserve.dto;

import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@Setter
public class RoomReserveCsvDto {
    private Long userId;
    private Long roomId;
    private LocalDateTime reservationDate;
    private LocalDateTime reservationDateEnd;
    private LocalDateTime backup_timeStamp;

    public RoomReserveCsvDto(RoomReserve roomReserve) {
        this.userId = roomReserve.getUser().getId();
        this.roomId = roomReserve.getRoom().getId();
        this.reservationDate = roomReserve.getReservationDate();
        this.reservationDateEnd = roomReserve.getReservationDateEnd();
        this.backup_timeStamp = LocalDateTime.now();
    }
}
