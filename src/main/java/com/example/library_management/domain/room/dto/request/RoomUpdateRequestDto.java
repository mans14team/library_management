package com.example.library_management.domain.room.dto.request;

import com.example.library_management.domain.room.enums.RoomStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomUpdateRequestDto {
    private String roomName;

    private RoomStatus roomStatus;
}
