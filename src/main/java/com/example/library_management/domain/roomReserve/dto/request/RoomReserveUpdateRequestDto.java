package com.example.library_management.domain.roomReserve.dto.request;

import com.example.library_management.domain.roomReserve.exception.AtLeastOneFieldRequiredException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
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
