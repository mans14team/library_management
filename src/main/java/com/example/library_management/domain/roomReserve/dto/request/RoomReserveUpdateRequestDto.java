package com.example.library_management.domain.roomReserve.dto.request;

import com.example.library_management.domain.roomReserve.exception.AtLeastOneFieldRequiredException;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NONE // 타입 정보를 사용하지 않음
)
@Getter
@AllArgsConstructor
public class RoomReserveUpdateRequestDto {

    private LocalDateTime reservationDate;

    private LocalDateTime reservationDateEnd;

    public void validate(){
        if(reservationDate == null && reservationDateEnd == null){
            throw new AtLeastOneFieldRequiredException();
        }
    }
}
