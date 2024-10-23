package com.example.library_management.domain.roomReserve.service;

import com.example.library_management.domain.roomReserve.dto.response.RoomReserveCreateResponseDto;
import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import com.example.library_management.domain.roomReserve.repository.RoomReserveRepository;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomReserveService {

    private final RoomReserveRepository roomReserveRepository;


    public RoomReserveCreateResponseDto createRoomReserve(UserDetailsImpl userDetails, Long roomId) {
        // 요청자 권한 확인

        // 로직

        RoomReserve roomReserve = new RoomReserve();
        roomReserveRepository.save(roomReserve);

        return new RoomReserveCreateResponseDto(roomReserve);
    }
}
