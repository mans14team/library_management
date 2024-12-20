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
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    public RoomCreateResponseDto createRoom(User user, RoomCreateRequestDto roomCreateRequestDto) {
        // 요청자 권한 확인
        validateRoomAccess(user);

        // 로직
        Room room = Room.createRoom(roomCreateRequestDto);

        Room savedRoom = roomRepository.save(room);

        return new RoomCreateResponseDto(savedRoom);
    }

    @Transactional
    public RoomUpdateResponseDto updateRoom(User user, Long roomId, RoomUpdateRequestDto roomUpdateRequestDto) {
        // 요청자 권한 확인
        validateRoomAccess(user);

        // 로직
        Room updatedRoom = findRoomById(roomId);

        updatedRoom.update(roomUpdateRequestDto);

        return new RoomUpdateResponseDto(updatedRoom);
    }

    @Transactional
    public RoomDeleteResponseDto deleteRoom(User user, Long roomId) {
        // 요청자 권한 확인
        validateRoomAccess(user);

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
    public void validateRoomAccess(User user){
        // 사용자가 ADMIN 권한을 가지고 있는지 확인
        boolean isAdmin = user.getRole().equals(UserRole.ROLE_ADMIN);

        if (!isAdmin) {
            throw new UnauthorizedRoomAccessException();
        }
    }
}
