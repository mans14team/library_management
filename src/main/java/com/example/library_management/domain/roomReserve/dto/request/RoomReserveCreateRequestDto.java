package com.example.library_management.domain.roomReserve.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RoomReserveCreateRequestDto {

    private final LocalDateTime reservationDate;

    private final LocalDateTime reservationDateEnd;

}
