package com.example.library_management.domain.room.dto.response;

import com.example.library_management.domain.room.entity.Room;
import com.example.library_management.domain.room.enums.RoomStatus;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
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
