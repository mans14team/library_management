package com.example.library_management.domain.room.dto.response;

import com.example.library_management.domain.room.entity.Room;
import com.example.library_management.domain.room.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomCreateResponseDto {

    private final Long id;

    private final String roomName;

    private final RoomStatus roomStatus;

    public RoomCreateResponseDto(Room room){
        this.id = room.getId();
        this.roomName = room.getRoomName();
        this.roomStatus = room.getRoomStatus();
    }
}
