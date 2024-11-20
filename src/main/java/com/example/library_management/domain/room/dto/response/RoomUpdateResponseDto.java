package com.example.library_management.domain.room.dto.response;

import com.example.library_management.domain.room.entity.Room;
import com.example.library_management.domain.room.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomUpdateResponseDto {

    private Long id;

    private String roomName;

    private RoomStatus roomStatus;

    public RoomUpdateResponseDto(Room updatedRoom) {
        this.id = updatedRoom.getId();
        this.roomName = updatedRoom.getRoomName();
        this.roomStatus = updatedRoom.getRoomStatus();
    }
}
