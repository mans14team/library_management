package com.example.library_management.domain.room.service;

import com.example.library_management.domain.room.dto.request.RoomCreateRequestDto;
import com.example.library_management.domain.room.dto.request.RoomUpdateRequestDto;
import com.example.library_management.domain.room.dto.response.RoomCreateResponseDto;
import com.example.library_management.domain.room.dto.response.RoomDeleteResponseDto;
import com.example.library_management.domain.room.dto.response.RoomGetResponseDto;
import com.example.library_management.domain.room.dto.response.RoomUpdateResponseDto;
import com.example.library_management.domain.room.entity.Room;
import com.example.library_management.domain.room.exception.NotFoundRoomException;
import com.example.library_management.domain.room.exception.UnauthorizedRoomAccessException;
import com.example.library_management.domain.room.repository.RoomRepository;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public RoomGetResponseDto getRoom(Long roomId) {
        // 로직
        Room room = findRoomById(roomId);

        return new RoomGetResponseDto(room);
    }

    @Transactional
    public RoomCreateResponseDto createRoom(UserDetailsImpl userDetails, RoomCreateRequestDto roomCreateRequestDto) {
        // 요청자 권한 확인
        validateRoomAccess(userDetails);

        // 로직
        Room room = Room.createRoom(roomCreateRequestDto);

        Room savedRoom = roomRepository.save(room);

        return new RoomCreateResponseDto(savedRoom);
    }

    @Transactional
    public RoomUpdateResponseDto updateRoom(UserDetailsImpl userDetails, Long roomId, RoomUpdateRequestDto roomUpdateRequestDto) {
        // 요청자 권한 확인
        validateRoomAccess(userDetails);

        // 로직
        Room updatedRoom = findRoomById(roomId);

        updatedRoom.update(roomUpdateRequestDto);

        return new RoomUpdateResponseDto(updatedRoom);
    }

    @Transactional
    public RoomDeleteResponseDto deleteRoom(UserDetailsImpl userDetails, Long roomId) {
        // 요청자 권한 확인
        validateRoomAccess(userDetails);

        // 로직
        Room deletedRoom = findRoomById(roomId);

        // CascadeType.REMOVE로 Room과 연관된 RoomReserve 자동 삭제.
        roomRepository.delete(deletedRoom);

        return new RoomDeleteResponseDto(deletedRoom);
    }

    // Room 조회
    public Room findRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(NotFoundRoomException::new);

        return room;
    }

    // 요청자 권한 확인
    public void validateRoomAccess(UserDetailsImpl userDetails){
        // 사용자가 ADMIN 권한을 가지고 있는지 확인
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        System.out.println("권한 :"+userDetails.getAuthorities());

        if (!isAdmin) {
            throw new UnauthorizedRoomAccessException();
        }
    }
}
