package com.example.library_management.domain.roomReserve.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomReserveCreateRequestDto {

    private LocalDateTime reservationDate;

    private LocalDateTime reservationDateEnd;

}
