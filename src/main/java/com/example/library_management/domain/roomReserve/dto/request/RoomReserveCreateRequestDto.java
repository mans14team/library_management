package com.example.library_management.domain.roomReserve.dto.request;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@AllArgsConstructor
public class RoomReserveCreateRequestDto {

    private final LocalDateTime reservationDate;

    private final LocalDateTime reservationDateEnd;

}
