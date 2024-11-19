package com.example.library_management.domain.room.dto.request;

import com.example.library_management.domain.room.enums.RoomStatus;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@AllArgsConstructor
public class RoomCreateRequestDto {

    @NotBlank
    private final String roomName;

    private final RoomStatus roomStatus;
}
