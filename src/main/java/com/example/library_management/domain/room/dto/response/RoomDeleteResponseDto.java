package com.example.library_management.domain.room.dto.response;

import com.example.library_management.domain.room.entity.Room;
import com.example.library_management.domain.room.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomDeleteResponseDto {

    private Long id;

    private String roomName;

    private RoomStatus roomStatus;

    public RoomDeleteResponseDto(Room deletedRoom) {
        this.id = deletedRoom.getId();
        this.roomName = deletedRoom.getRoomName();
        this.roomStatus = deletedRoom.getRoomStatus();
    }
}
