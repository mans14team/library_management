package com.example.library_management.domain.room.service;

import com.example.library_management.domain.room.dto.request.RoomCreateRequestDto;
import com.example.library_management.domain.room.dto.request.RoomUpdateRequestDto;
import com.example.library_management.domain.room.dto.response.RoomCreateResponseDto;
import com.example.library_management.domain.room.dto.response.RoomDeleteResponseDto;
import com.example.library_management.domain.room.dto.response.RoomUpdateResponseDto;
import com.example.library_management.domain.room.entity.Room;
import com.example.library_management.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional
    public RoomCreateResponseDto createRoom(RoomCreateRequestDto roomCreateRequestDto) {
        // JWT 토큰 -> 요청자 권한 확인, ADMIN인지 확인

        // 로직
        Room room = Room.createRoom(roomCreateRequestDto);

        Room savedRoom = roomRepository.save(room);

        return new RoomCreateResponseDto(savedRoom);
    }

    @Transactional
    public RoomUpdateResponseDto updateRoom(Long roomId, RoomUpdateRequestDto roomUpdateRequestDto) {
        // JWT 토큰 -> 요청자 권한 확인, ADMIN인지 확인

        // 로직   -> CustomException 변경해야함.
        Room updatedRoom = findRoomById(roomId);

        updatedRoom.update(roomUpdateRequestDto);

        return new RoomUpdateResponseDto(updatedRoom);
    }

    @Transactional
    public RoomDeleteResponseDto deleteRoom(Long roomId) {
        // JWT 토큰 -> 요청자 권한 확인, ADMIN인지 확인

        // 로직
        Room deletedRoom = findRoomById(roomId);

        // CascadeType.REMOVE로 Room과 연관된 RoomReserve 자동 삭제.
        roomRepository.delete(deletedRoom);

        return new RoomDeleteResponseDto(deletedRoom);
    }

    // Room 조회
    public Room findRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("전달된 id 값에 해당하는 Room 엔티티가 존재하지 않습니다."));

        return room;
    }
}
