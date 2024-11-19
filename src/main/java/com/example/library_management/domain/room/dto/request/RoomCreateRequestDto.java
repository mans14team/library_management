package com.example.library_management.domain.room.dto.request;

import com.example.library_management.domain.room.enums.RoomStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomCreateRequestDto {

    @NotBlank
    private String roomName;

    private RoomStatus roomStatus;
}
