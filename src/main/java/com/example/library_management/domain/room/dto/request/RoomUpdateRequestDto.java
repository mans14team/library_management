package com.example.library_management.domain.room.dto.request;

import com.example.library_management.domain.room.enums.RoomStatus;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@NoArgsConstructor
public class RoomUpdateRequestDto {
    private String roomName;

    private RoomStatus roomStatus;
}
