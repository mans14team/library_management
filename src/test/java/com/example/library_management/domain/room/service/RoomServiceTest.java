package com.example.library_management.domain.room.service;

import com.example.library_management.domain.common.exception.GlobalExceptionConst;
import com.example.library_management.domain.room.dto.request.RoomCreateRequestDto;
import com.example.library_management.domain.room.dto.request.RoomUpdateRequestDto;
import com.example.library_management.domain.room.dto.response.RoomCreateResponseDto;
import com.example.library_management.domain.room.dto.response.RoomGetResponseDto;
import com.example.library_management.domain.room.dto.response.RoomUpdateResponseDto;
import com.example.library_management.domain.room.entity.Room;
import com.example.library_management.domain.room.enums.RoomStatus;
import com.example.library_management.domain.room.exception.NotFoundRoomException;
import com.example.library_management.domain.room.exception.UnauthorizedRoomAccessException;
import com.example.library_management.domain.room.repository.RoomRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {
    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roomService = new RoomService(roomRepository);
    }

    @Nested
    @DisplayName("스터디룸 조회 테스트")
    class GetRoom{
        @Test
        public void 스터디룸_조회_성공(){
            // given
            Room room = new Room();
            ReflectionTestUtils.setField(room, "id", 1L);
            ReflectionTestUtils.setField(room, "roomName", "스터디룸");
            ReflectionTestUtils.setField(room, "roomStatus", RoomStatus.AVAILABLE);

            // Mockito로 가짜동작을 정의하는 코드, -> 특정 roomId에 대해 Room 객체가 반환되는 상황을 가정.
            when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

            // when
            RoomGetResponseDto result = roomService.getRoom(1L);

            // then
            assertNotNull(result);
            assertEquals("스터디룸", result.getRoomName());
            assertEquals(RoomStatus.AVAILABLE, result.getRoomStatus());
            verify(roomRepository, times(1)).findById(1L);
        }

        @Test
        public void 스터디룸_조회_실패() {
            // given
            when(roomRepository.findById(1L)).thenReturn(Optional.empty());

            // when, then
            assertThrows(NotFoundRoomException.class, () -> roomService.getRoom(1L));
            verify(roomRepository, times(1)).findById(1L);
        }
    }

    @Nested
    @DisplayName("스터디룸 생성 테스트")
    class CreateRoom{

        @Test
        public void ROLE_ADMIN이_스터디룸_생성_성공(){
            // given
            User user = new User();
            ReflectionTestUtils.setField(user, "id", 1L);
            ReflectionTestUtils.setField(user, "role", UserRole.ROLE_ADMIN);

            RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto("스터디룸1", RoomStatus.AVAILABLE);

            Room room = Room.createRoom(roomCreateRequestDto);
            given(roomRepository.save(any(Room.class))).willReturn(room);

            // when
            RoomCreateResponseDto roomCreateResponseDto = roomService.createRoom(user, roomCreateRequestDto);

            // then
            assertNotNull(roomCreateResponseDto);
            assertEquals("스터디룸1", roomCreateResponseDto.getRoomName());
            assertEquals(RoomStatus.AVAILABLE, roomCreateResponseDto.getRoomStatus());
        }

        @Test
        public void ROLE_USER가_스터디룸_생성_실패(){
            // given
            User user = new User();
            ReflectionTestUtils.setField(user, "id", 2L);
            ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);

            RoomCreateRequestDto roomCreateRequestDto = new RoomCreateRequestDto("스터디룸2", RoomStatus.AVAILABLE);

            // when & then
            UnauthorizedRoomAccessException exception = assertThrows(UnauthorizedRoomAccessException.class,
                    () -> roomService.createRoom(user, roomCreateRequestDto));

            assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains(GlobalExceptionConst.FORBIDDEN_ROOMCONTROL.getMessage()));
            verify(roomRepository,never()).save(any(Room.class));

        }
    }

    @Nested
    @DisplayName("스터디룸 수정 테스트")
    class UpdateRoom{

        @Test
        public void ROLE_ADMIN가_스터디룸_수정_성공(){
            // given
            User user = new User();
            ReflectionTestUtils.setField(user, "id", 1L);
            ReflectionTestUtils.setField(user, "role", UserRole.ROLE_ADMIN);

            Room existingRoom = new Room();
            ReflectionTestUtils.setField(existingRoom, "id", 2L);
            ReflectionTestUtils.setField(existingRoom, "roomName", "수터디룸");
            ReflectionTestUtils.setField(existingRoom, "roomStatus", RoomStatus.NON_AVAILABLE);

            RoomUpdateRequestDto roomUpdateRequestDto = new RoomUpdateRequestDto();
            ReflectionTestUtils.setField(roomUpdateRequestDto, "roomName", "스터디룸");
            ReflectionTestUtils.setField(roomUpdateRequestDto, "roomStatus", RoomStatus.AVAILABLE);

            when(roomRepository.findById(2L)).thenReturn(Optional.of(existingRoom));

            // when
            RoomUpdateResponseDto response = roomService.updateRoom(user,2L, roomUpdateRequestDto);

            // then
            assertEquals("스터디룸", response.getRoomName());
            assertEquals(RoomStatus.AVAILABLE, response.getRoomStatus());
        }

        @Test
        public void ROLE_USER가_스터디룸_수정_실패(){
            // given
            User user = new User();
            ReflectionTestUtils.setField(user, "id", 2L);
            ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);

            Room existingRoom = new Room();
            ReflectionTestUtils.setField(existingRoom, "id", 2L);
            ReflectionTestUtils.setField(existingRoom, "roomName", "수터디룸");
            ReflectionTestUtils.setField(existingRoom, "roomStatus", RoomStatus.NON_AVAILABLE);

            RoomUpdateRequestDto roomUpdateRequestDto = new RoomUpdateRequestDto();
            ReflectionTestUtils.setField(roomUpdateRequestDto, "roomName", "스터디룸");
            ReflectionTestUtils.setField(roomUpdateRequestDto, "roomStatus", RoomStatus.AVAILABLE);

            // when, then
            UnauthorizedRoomAccessException exception = assertThrows(UnauthorizedRoomAccessException.class,
                    () -> roomService.updateRoom(user, 2L, roomUpdateRequestDto));

            assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
            assertTrue(exception.getMessage().contains(GlobalExceptionConst.FORBIDDEN_ROOMCONTROL.getMessage()));
        }
    }

    @Nested
    @DisplayName("스터디룸 삭제 테스트")
    class DeleteRoom{
        @Test
        public void ROLE_ADMIN가_스터디룸_삭제_성공(){
            // given
            User user = new User();
            ReflectionTestUtils.setField(user, "id", 1L);
            ReflectionTestUtils.setField(user, "role", UserRole.ROLE_ADMIN);

            Room existingRoom = new Room();
            ReflectionTestUtils.setField(existingRoom, "id", 1L);
            ReflectionTestUtils.setField(existingRoom, "roomName", "스터디룸");
            ReflectionTestUtils.setField(existingRoom, "roomStatus", RoomStatus.AVAILABLE);

            when(roomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));

            // when
            roomService.deleteRoom(user, 1L);

            // then
            // delete가 1번 호출되었는지를 검증하는 코드.
            verify(roomRepository, times(1)).delete(existingRoom);

        }

        @Test
        public void ROLE_USER가_스터디룸_삭제_실패(){
            // given
            User user = new User();
            ReflectionTestUtils.setField(user, "id", 1L);
            ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);

            Room room = new Room();
            ReflectionTestUtils.setField(room, "id", 2L);
            ReflectionTestUtils.setField(room, "roomName", "스터디룸");

            // when, then
            assertThrows(UnauthorizedRoomAccessException.class,
                    () -> roomService.deleteRoom(user, 2L));

            // delete가 한번도 호출되지않음을 검증하는 코드.
            verify(roomRepository, never()).delete(any(Room.class));

        }

        @Test
        public void 존재하지_않는_스터디룸_삭제_실패(){
            // given
            User user = new User();
            ReflectionTestUtils.setField(user, "id", 1L);
            ReflectionTestUtils.setField(user, "role", UserRole.ROLE_ADMIN);

            when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

            // when, then
            assertThrows(NotFoundRoomException.class,
                    () -> roomService.deleteRoom(user, 1L));

            verify(roomRepository, never()).delete(any(Room.class));

        }
    }

}
