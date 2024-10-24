package com.example.library_management.domain.room.dto.request;

import com.example.library_management.domain.room.enums.RoomStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomCreateRequestDto {

    @NotBlank
    private final String roomName;

    private final RoomStatus roomStatus;
}
