package com.example.library_management.domain.roomReserve.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RoomReserveCreateRequestDto {

    @NotBlank
    private final LocalDateTime reservationDate;

    @NotBlank
    private final LocalDateTime reservationDateEnd;

}
