package com.example.library_management.domain.roomReserve.service;

import com.example.library_management.domain.room.entity.Room;
import com.example.library_management.domain.room.enums.RoomStatus;
import com.example.library_management.domain.room.service.RoomService;
import com.example.library_management.domain.roomReserve.dto.request.RoomReserveCreateRequestDto;
import com.example.library_management.domain.roomReserve.dto.request.RoomReserveUpdateRequestDto;
import com.example.library_management.domain.roomReserve.dto.response.RoomReserveCreateResponseDto;
import com.example.library_management.domain.roomReserve.dto.response.RoomReserveDeleteResponseDto;
import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import com.example.library_management.domain.roomReserve.exception.*;
import com.example.library_management.domain.roomReserve.repository.RoomReserveRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomReserveServiceTest {

    @Mock
    private RoomReserveRepository roomReserveRepository;

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomReserveService roomReserveService;

    private User user;
    private Room room;
    private List<RoomReserve> roomReserveList;

    @BeforeEach
    void setUp() {
        user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "role", UserRole.ROLE_USER);

        room = new Room();
        ReflectionTestUtils.setField(room, "id", 2L);
        ReflectionTestUtils.setField(room, "roomStatus", RoomStatus.AVAILABLE);

        // RoomReserve 리스트 test Data init
        roomReserveList = new ArrayList<>();

        // 11~19시까지 2시간 간격의 예약 생성
        for (int i = 1; i < 5; i++) {
            RoomReserve roomReserve = new RoomReserve();
            ReflectionTestUtils.setField(roomReserve, "id", (long) i);
            ReflectionTestUtils.setField(roomReserve, "reservationDate", LocalDateTime.of(2024, 10, 27, 2 * i + 9, 0));
            ReflectionTestUtils.setField(roomReserve, "reservationDateEnd", LocalDateTime.of(2024, 10, 27, (2 * i + 2) + 9, 0));
            ReflectionTestUtils.setField(roomReserve, "user", user);
            ReflectionTestUtils.setField(roomReserve, "room", room);
            roomReserveList.add(roomReserve);
        }

        ReflectionTestUtils.setField(room, "roomReservations", roomReserveList);
    }

    @Nested
    @DisplayName("스터디룸 예약 생성 테스트")
    class createRoomReserveTest {

        @Test
        public void 생성가능상태의_스터디룸_중복되지않는시간_예약_성공() {
            // given
            when(roomService.findRoomById(room.getId())).thenReturn(room);
            when(roomReserveRepository.findAllByRoomId(room.getId())).thenReturn(roomReserveList);
            when(roomReserveRepository.save(any(RoomReserve.class))).thenAnswer(invocation -> {
                RoomReserve savedReserve = invocation.getArgument(0);
                ReflectionTestUtils.setField(savedReserve, "id", 5L); // 새로 저장되는 RoomReserve의 id
                return savedReserve;
            });

            // 19~21 시 까지의 기존 예약과 중복되지 않는 예약
            LocalDateTime reservationDate = LocalDateTime.of(2024, 10, 27, 19, 0);
            LocalDateTime reservationDateEnd = LocalDateTime.of(2024, 10, 27, 21, 0);

            RoomReserveCreateRequestDto roomReserveCreateRequestDto = new RoomReserveCreateRequestDto(reservationDate, reservationDateEnd);

            // when
            RoomReserveCreateResponseDto responseDto = roomReserveService.createRoomReserve(user, room.getId(), roomReserveCreateRequestDto);

            // then
            assertNotNull(responseDto);
            assertEquals(roomReserveCreateRequestDto.getReservationDate(), responseDto.getReservationDate());
            assertEquals(roomReserveCreateRequestDto.getReservationDateEnd(), responseDto.getReservationDateEnd());
            verify(roomReserveRepository, times(1)).save(any(RoomReserve.class));

        }

        @Test
        public void 생성불가능상태의_스터디룸_예약_실패() {
            // given
            when(roomService.findRoomById(room.getId())).thenReturn(room);
            ReflectionTestUtils.setField(room, "roomStatus", RoomStatus.NON_AVAILABLE);
            RoomReserveCreateRequestDto roomReserveCreateRequestDto = new RoomReserveCreateRequestDto(LocalDateTime.now(), LocalDateTime.now());

            // when & then
            assertThrows(RoomReserveUnavailableException.class, () -> {
                roomReserveService.createRoomReserve(user, room.getId(), roomReserveCreateRequestDto);
            });
        }

        @Test
        public void 스터디룸_예약시간_중복_예약_실패() {
            // given
            when(roomService.findRoomById(room.getId())).thenReturn(room);
            when(roomReserveRepository.findAllByRoomId(room.getId())).thenReturn(roomReserveList);

            // 15~17 시 까지의 기존 예약과 중복되는 예약
            LocalDateTime reservationDate = LocalDateTime.of(2024, 10, 27, 15, 0);
            LocalDateTime reservationDateEnd = LocalDateTime.of(2024, 10, 27, 17, 0);

            RoomReserveCreateRequestDto roomReserveCreateRequestDto = new RoomReserveCreateRequestDto(reservationDate, reservationDateEnd);

            // when & then
            assertThrows(RoomReserveOverlapException.class, () -> {
                roomReserveService.createRoomReserve(user, room.getId(), roomReserveCreateRequestDto);
            });
        }

        // 낙관적 락 발생 by multithread ???
        @Test
        void 멀티스레드사용_낙관적락_적용_예외처리_확인() throws InterruptedException{
            // Given
            RoomReserveCreateRequestDto roomReserveCreateRequestDto = new RoomReserveCreateRequestDto(LocalDateTime.of(2024,10,27,10,0),
                    LocalDateTime.of(2024,10,27,12,0));

            when(roomService.findRoomById(any(Long.class))).thenReturn(room);

            // 낙관적 락이 발생하도록 설정
            doThrow(new OptimisticLockingFailureException("Lock exception"))
                    .when(roomReserveRepository).save(any(RoomReserve.class));

            // 멀티스레드 환경을 설정
            ExecutorService executor = Executors.newFixedThreadPool(2);
            CountDownLatch latch = new CountDownLatch(2);
            Exception[] exceptionHolder = new Exception[1];

            // 두 개의 스레드에서 동시에 예약을 시도
            Runnable task = () -> {
                try {
                    roomReserveService.createRoomReserve(user, room.getId(), roomReserveCreateRequestDto);
                } catch (Exception e) {
                    exceptionHolder[0] = e;
                } finally {
                    latch.countDown();
                }
            };

            // 두 스레드 실행
            executor.submit(task);
            executor.submit(task);

            // 모든 스레드가 종료될 때까지 대기
            latch.await(5, TimeUnit.SECONDS);
            executor.shutdown();

            // Then
            assertNotNull(exceptionHolder[0]);
            assertTrue(exceptionHolder[0] instanceof OptimisticLockConflictException);
            verify(roomReserveRepository, times(2)).save(any(RoomReserve.class)); // 두 번 호출 확인
        }

    }

    @Nested
    @DisplayName("스터디룸 예약 수정 테스트")
    class UpdateRoomReserveTest {

        @Test
        public void 중복되지않는_예약_시간으로_예약_수정_성공(){
            // given
            //9~11로 중복되지 않는 예약 시간
            LocalDateTime newReservationDate = LocalDateTime.of(2024, 10, 27, 9, 0);
            LocalDateTime newReservationDateEnd = LocalDateTime.of(2024, 10, 27, 11, 0);

            // 예약 수정 요청 DTO 생성
            RoomReserveUpdateRequestDto roomReserveUpdateRequestDto = new RoomReserveUpdateRequestDto(newReservationDate, newReservationDateEnd);

            // 수정할 예약의 ID
            Long reserveIdToUpdate = 1L;

            // when
            RoomReserve filteredRoomReserve = room.getRoomReservations().stream()
                    .filter(reserve -> reserve.getId().equals(reserveIdToUpdate))
                    .findFirst()
                    .orElseThrow(NotFoundRoomReserveException::new);

            if (roomReserveUpdateRequestDto.getReservationDate() != null) {
                filteredRoomReserve.updateReservationDate(roomReserveUpdateRequestDto.getReservationDate());
            }
            if (roomReserveUpdateRequestDto.getReservationDateEnd() != null) {
                filteredRoomReserve.updateReservationDateEnd(roomReserveUpdateRequestDto.getReservationDateEnd());
            }

            // then
            assertEquals(newReservationDate, filteredRoomReserve.getReservationDate());
            assertEquals(newReservationDateEnd, filteredRoomReserve.getReservationDateEnd());
        }

        @Test
        public void 중복되는_예약_시간으로_예약_수정_실패(){

            // given
            LocalDateTime newReservationDate = LocalDateTime.of(2024, 10, 27, 15, 0); // 새로운 시작 시간 (중복)
            LocalDateTime newReservationDateEnd = LocalDateTime.of(2024, 10, 27, 17, 0); // 새로운 종료 시간 (중복)

            RoomReserveUpdateRequestDto roomReserveUpdateRequestDto = new RoomReserveUpdateRequestDto(newReservationDate,newReservationDateEnd);

            // 수정할 예약의 ID
            Long reserveIdToUpdate = 1L;

            // when
            RoomReserve filteredRoomReserve = room.getRoomReservations().stream()
                    .filter(reserve -> reserve.getId().equals(reserveIdToUpdate))
                    .findFirst()
                    .orElseThrow(NotFoundRoomReserveException::new);

            // 예약 시간이 중복되는지 체크
            List<RoomReserve> roomReserveList = room.getRoomReservations();

            // 예약 수정 요청을 위한 새로운 시작 및 종료 시간
            LocalDateTime reservationDate = roomReserveUpdateRequestDto.getReservationDate();
            LocalDateTime reservationDateEnd = roomReserveUpdateRequestDto.getReservationDateEnd();

            if (roomReserveUpdateRequestDto.getReservationDate() != null) {
                filteredRoomReserve.updateReservationDate(roomReserveUpdateRequestDto.getReservationDate());
            }
            if (roomReserveUpdateRequestDto.getReservationDateEnd() != null) {
                filteredRoomReserve.updateReservationDateEnd(roomReserveUpdateRequestDto.getReservationDateEnd());
            }

            // then
            assertThrows(RoomReserveOverlapException.class, () -> {
                for (RoomReserve existingReserve : roomReserveList) {
                    // 기존 예약 시간과 비교하여 중복 여부 체크
                    if (!existingReserve.getId().equals(filteredRoomReserve.getId())) {
                        // 중복 체크 로직
                        boolean isOverlap = reservationDate.isBefore(existingReserve.getReservationDateEnd()) &&
                                reservationDateEnd.isAfter(existingReserve.getReservationDate());

                        if (isOverlap) {
                            throw new RoomReserveOverlapException(); // 중복이 발생하면 예외 발생
                        }
                    }
                }
            });
        }

        @Test
        public void 예약자와_요청자_ID불일치로_예약_수정_실패(){
            // given
            // 기존 예약자는 1L, Room Id는 2L
            User requestUser = new User();
            ReflectionTestUtils.setField(requestUser, "id", 2L); // 요청자의 ID가 예약자의 ID와 다름

            when(roomService.findRoomById(room.getId())).thenReturn(room);

            RoomReserveUpdateRequestDto roomReserveUpdateRequestDto = new RoomReserveUpdateRequestDto(
                    LocalDateTime.of(2024,10,28,14,16,0),
                    LocalDateTime.of(2024,10,28,14,18,0)
            );

            // when & then
            assertThrows(ReservationModificationNotAllowedException.class,
                    () -> roomReserveService.updateRoomReserve(requestUser, 2L, 1L, roomReserveUpdateRequestDto));

        }

        @Test
        public void 존재하지않는_스터디룸_예약_수정_실패() {
            // given
            when(roomService.findRoomById(room.getId())).thenReturn(room);

            RoomReserveUpdateRequestDto roomReserveUpdateRequestDto = new RoomReserveUpdateRequestDto(
                    LocalDateTime.of(2024, 10, 27, 21, 0),
                    LocalDateTime.of(2024, 10, 27, 22, 0)
            );

            // when & then
            assertThrows(NotFoundRoomReserveException.class,
                    () -> roomReserveService.updateRoomReserve(user, 2L, 5L, roomReserveUpdateRequestDto));
        }
    }

    @Nested
    @DisplayName("스터디룸 예약 삭제 테스트")
    class deleteRoomReserveTest {

        @Test
        void 스터디룸_예약_삭제_성공() {
            // given
            when(roomService.findRoomById(room.getId())).thenReturn(room);

            // when
            RoomReserveDeleteResponseDto responseDto = roomReserveService.deleteRoomReserve(user, room.getId(), 1L);

            // then
            assertNotNull(responseDto);
            assertEquals(1L, responseDto.getId());
            verify(roomReserveRepository, times(1)).delete(any(RoomReserve.class));
        }

        @Test
        void 다른유저의_스터디룸_예약_삭제_실패() {
            // given
            // 1L User가 기존 유저
            User differentUser = new User();
            ReflectionTestUtils.setField(differentUser, "id", 2L);
            
            when(roomService.findRoomById(room.getId())).thenReturn(room);

            // when & then
            assertThrows(ReservationDeleteNotAllowedException.class, () -> {
                roomReserveService.deleteRoomReserve(differentUser, room.getId(), 1L); // 다른 유저가 삭제 시도
            });

            verify(roomReserveRepository, never()).delete(any(RoomReserve.class));
        }
    }
}
