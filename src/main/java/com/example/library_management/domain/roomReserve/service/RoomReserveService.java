package com.example.library_management.domain.roomReserve.service;

import com.example.library_management.domain.room.entity.Room;
import com.example.library_management.domain.room.enums.RoomStatus;
import com.example.library_management.domain.room.service.RoomService;
import com.example.library_management.domain.roomReserve.dto.request.RoomReserveCreateRequestDto;
import com.example.library_management.domain.roomReserve.dto.request.RoomReserveUpdateRequestDto;
import com.example.library_management.domain.roomReserve.dto.response.RoomReserveCreateResponseDto;
import com.example.library_management.domain.roomReserve.dto.response.RoomReserveDeleteResponseDto;
import com.example.library_management.domain.roomReserve.dto.response.RoomReserveResponseDto;
import com.example.library_management.domain.roomReserve.dto.response.RoomReserveUpdateResponseDto;
import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import com.example.library_management.domain.roomReserve.exception.*;
import com.example.library_management.domain.roomReserve.repository.RoomReserveRepository;
import com.example.library_management.domain.user.entity.User;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomReserveService {

    private final RoomReserveRepository roomReserveRepository;
    private final RoomService roomService;

    @Transactional
    public RoomReserveCreateResponseDto createRoomReserve(UserDetailsImpl userDetails, Long roomId, RoomReserveCreateRequestDto roomReserveCreateRequestDto) {
        // 요청자 권한 확인    - 멤버쉽 권한 만이 스터디룸 예약을 할 수 있다.
        // User 객체의 멤버쉽 권한 확인 로직 미추가상태. - 10/23
        User user = userDetails.getUser();

        // 예약하고자 하는 roomId 의 예약 가능 여부 확인.
        /*
            RoomStatus -> AVAILABLE
            if 입력받은 예약시간 정보가 예약 가능한가? -> 로직 수행.
            else -> Exception throw

            RoomStatus -> NON_AVAILABLE
            if 관리자에 의해 폐쇠한 상태이다 -> Exception throw (RoomStatus enum 수정필요 부분)
            else -> Exception throw (모든 시간이 예약되어 예약 불가능한 상태)
         */
        Room room = roomService.findRoomById(roomId);

        if (room.getRoomStatus() == RoomStatus.NON_AVAILABLE) {
            throw new RoomReserveUnavailableException();
        }

        /*
         RoomReserveCreateRequestDto의 reservationDate와 reservationDateEnd가 기존의 예약과 겹치는지 유무 판별.
         예약이 겹치는 Case
         Case 1: 새로운 예약의 reservationDate가 기존 예약 사이에 있는 경우
         Case 2: 새로운 예약의 reservationDateEnd가 기존 예약 사이에 있는 경우
         Case 3: 새로운 예약이 기존 예약을 덮거나, 기존 예약이 새로운 예약을 덮는 경우.
         */

        List<RoomReserve> roomReserveList = room.getRoomReservations();

        LocalDateTime reservationDate = roomReserveCreateRequestDto.getReservationDate();
        LocalDateTime reservationDateEnd = roomReserveCreateRequestDto.getReservationDateEnd();

        for (RoomReserve existingReserve : roomReserveList) {
            // 기존 예약 시간
            LocalDateTime existingStartTime = existingReserve.getReservationDate();
            LocalDateTime existingEndTime = existingReserve.getReservationDateEnd();

            // 예약이 겹치는 Case 3가지를 판별하는 로직.
            boolean isOverlap = reservationDate.isBefore(existingEndTime) && reservationDateEnd.isAfter(existingStartTime);

            // 겹치는 예약? -> Exception
            if (isOverlap) {
                throw new RoomReserveOverlapException();
            }
        }

        // 로직

        RoomReserve roomReserve = RoomReserve.createReservation(room, user, roomReserveCreateRequestDto);

        RoomReserve savedRoomReserve = roomReserveRepository.save(roomReserve);

        return new RoomReserveCreateResponseDto(savedRoomReserve);
    }

    @Transactional
    public RoomReserveUpdateResponseDto updateRoomReserve(UserDetailsImpl userDetails, Long roomId, Long reserveId, RoomReserveUpdateRequestDto roomReserveUpdateRequestDto) {
        // 요청자 권한 확인    - 해당 스터디룸 예약을 했던 유저만이 스터디룸 예약을 수정 할 수 있다.
        User user = userDetails.getUser();

        // 예약 정보 확인.
        Room room = roomService.findRoomById(roomId);

        RoomReserve filteredRoomReserve = room.getRoomReservations().stream()
                .filter(reserve -> reserve.getId().equals(reserveId))
                .findFirst().orElseThrow(NotFoundRoomReserveException::new);


        // 예약자의 ID와 요청자의 ID가 동일한지 검증
        if (!filteredRoomReserve.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new ReservationModificationNotAllowedException();
        }

        // 수정 요청받은 시간 정보가 기존 예약과 겹치는지 판별    -   10/24 현재 다른 사용자의 예약정보와 겹칠 수 있는 문제가 있음.
        List<RoomReserve> roomReserveList = room.getRoomReservations();

        // 요청받은 예약 시간 변경 정보 - 둘 중 하나 이상의 값이 전달.
        if (roomReserveUpdateRequestDto.getReservationDate() != null) {
            filteredRoomReserve.updateReservationDate(roomReserveUpdateRequestDto.getReservationDate());
        }
        if (roomReserveUpdateRequestDto.getReservationDateEnd() != null) {
            filteredRoomReserve.updateReservationDateEnd(roomReserveUpdateRequestDto.getReservationDateEnd());
        }

        LocalDateTime reservationDate = filteredRoomReserve.getReservationDate();
        LocalDateTime reservationDateEnd = filteredRoomReserve.getReservationDateEnd();

        for (RoomReserve existingReserve : roomReserveList) {
            // 기존의 자신의 예약과의 비교는 제외
            if (!existingReserve.getId().equals(filteredRoomReserve.getId())) {
                // 기존 예약 시간
                LocalDateTime existingStartTime = existingReserve.getReservationDate();
                LocalDateTime existingEndTime = existingReserve.getReservationDateEnd();

                // 예약이 겹치는 Case 3가지를 판별하는 로직.
                boolean isOverlap = reservationDate.isBefore(existingEndTime) && reservationDateEnd.isAfter(existingStartTime);

                // 겹치는 예약? -> Exception
                if (isOverlap) {
                    throw new RoomReserveOverlapException();
                }
            }
        }

        return new RoomReserveUpdateResponseDto(filteredRoomReserve);
    }

    @Transactional
    public RoomReserveDeleteResponseDto deleteRoomReserve(UserDetailsImpl userDetails, Long roomId, Long reserveId) {
        // 요청자 권한 확인 - 멤버쉽 권한이 아니면 예외처리.
        User user = userDetails.getUser();

        // 예약 정보 확인.
        Room room = roomService.findRoomById(roomId);

        RoomReserve filteredRoomReserve = room.getRoomReservations().stream()
                .filter(reserve -> reserve.getId().equals(reserveId))
                .findFirst().orElseThrow(NotFoundRoomReserveException::new);

        // 스터디룸 예약을 했던 유저만이 스터디룸 예약을 삭제 할 수 있다.
        if (!filteredRoomReserve.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new ReservationDeleteNotAllowedException();
        }

        roomReserveRepository.delete(filteredRoomReserve);

        return new RoomReserveDeleteResponseDto(filteredRoomReserve);
    }

    /*
        roomId를 가지는 Room의 모든 RoomReserv를 Get -> 권한 확인 X
        비 로그인 상태에서 해당 스터디룸의 예약상황을 확인하는데에 사용.
     */
    @Transactional(readOnly = true)
    public Page<RoomReserveResponseDto> findAllRoomReserve(int page, int size, Long roomId) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<RoomReserve> roomReservePage = roomReserveRepository.findAllByRoomId(roomId, pageable);

        return roomReservePage.map(RoomReserveResponseDto::new);
    }

    /*
        roomId를 가지는 Room의 RoomReserve에서 userDetails의 user가 신청한 RoomReserve들을 Get
     */
    @Transactional(readOnly = true)
    public Page<RoomReserveResponseDto> findAllRoomReserveByUser(int page, int size, UserDetailsImpl userDetails, Long roomId) {
        Pageable pageable = PageRequest.of(page - 1, size);  // 페이지는 0부터 시작
        Long userId = userDetails.getUser().getId();

        Page<RoomReserve> roomReservePage = roomReserveRepository.findAllByUserIdAndRoomId(userId, roomId, pageable);

        return roomReservePage.map(RoomReserveResponseDto::new);

    }
}
