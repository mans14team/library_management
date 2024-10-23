package com.example.library_management.domain.room.dto.response;

import com.example.library_management.domain.room.entity.Room;
import com.example.library_management.domain.room.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoomGetResponseDto {

    private String roomName;

    private RoomStatus roomStatus;

    public RoomGetResponseDto(Room room){
        this.roomName = room.getRoomName();
        this.roomStatus = room.getRoomStatus();
    }
}
