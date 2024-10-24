package com.example.library_management.domain.roomReserve.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RoomReserveCreateRequestDto {

    private final LocalDateTime reservationDate;

    private final LocalDateTime reservationDateEnd;

}
