package com.example.library_management.domain.roomReserve.service;

import com.example.library_management.domain.membership.enums.MembershipStatus;
import com.example.library_management.domain.membership.exception.MembershipCancelledException;
import com.example.library_management.domain.membership.exception.MembershipExpiredException;
import com.example.library_management.domain.membership.exception.MembershipNotFoundException;
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
import com.example.library_management.global.config.CustomConfig;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomReserveService {

    private final RoomReserveRepository roomReserveRepository;
    private final RoomService roomService;
    private final RedissonClient redissonClient;
    private final CustomConfig customConfig;

    /*
        예약하고자 하는 roomId 의 예약 가능 여부 확인.

        RoomStatus -> AVAILABLE
        if 입력받은 예약시간 정보가 예약 가능한가? -> 로직 수행.
        else -> Exception throw

        RoomStatus -> NON_AVAILABLE
        if 관리자에 의해 폐쇠한 상태이다 -> Exception throw (RoomStatus enum 수정필요 부분)
        else -> Exception throw (모든 시간이 예약되어 예약 불가능한 상태)

        도입하고자 하는 시스템은  대학교라고 가정
        중간고사 시험기간 4~5월
        기말고사 시험기간 6~7월
        LocalDateTime 타입의 getMonthValue()로 월 값을 추출하여 Redis 분산 락 사용여부를 결정.
     */

    @Transactional
    public RoomReserveCreateResponseDto createRoomReserve(User user, Long roomId, RoomReserveCreateRequestDto roomReserveCreateRequestDto) {
        // 요청자 권한 확인    - 멤버쉽 권한 만이 스터디룸 예약을 할 수 있다.
        //validateUserMembership(user);

        LocalDateTime now = LocalDateTime.now();
        int currentMonth = now.getMonthValue();

        // 현재의 Month값이 트래픽이 몰리는 기간인 경우(yml파일에서 가져온 정보를 바탕으로) Redis 분산락과 낙관적 락을 혼용.
        List<Integer> activeMonths = customConfig.getActiveMonths();
        boolean useRedis = activeMonths.contains(currentMonth);

        RLock lock = redissonClient.getLock("RoomReserveLock:" + roomId);

        try {
            if (useRedis) {
                if (lock.tryLock(3, 1, TimeUnit.SECONDS)) {
                    // Redis 분산락 + 낙관적 락 혼용 스터디룸 예약 진행
                    return processRoomReservation(user, roomId, roomReserveCreateRequestDto);
                } else {
                    throw new ReservationLockTimeOutException();
                }
            } else {
                // 낙관적 락만 사용 스터디룸 예약 진행
                return processRoomReservation(user, roomId, roomReserveCreateRequestDto);
            }
        } catch (InterruptedException e) {
            throw new RoomReserveException();
        } finally {
            if (useRedis && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional
    public RoomReserveUpdateResponseDto updateRoomReserve(User user, Long roomId, Long reserveId, RoomReserveUpdateRequestDto roomReserveUpdateRequestDto) {
        // 요청자 권한 확인    - 해당 스터디룸 예약을 했던 유저만이 스터디룸 예약을 수정 할 수 있다.
        // 중복 뜨는건, 추후 AOP 고려
        //validateUserMembership(user);

        // 요청 날짜 확인
        LocalDateTime now = LocalDateTime.now();
        int currentMonth = now.getMonthValue();

        List<Integer> activeMonths = customConfig.getActiveMonths();
        boolean useRedis = activeMonths.contains(currentMonth);

        RLock lock = redissonClient.getLock("RoomReserveLock:" + roomId);

        // 예약 정보 확인.
        Room room = roomService.findRoomById(roomId);
        RoomReserve filteredRoomReserve = findRoomReserveById(room, reserveId);

        // 예약자의 ID와 요청자의 ID가 동일한지 검증
        validateUserReservation(user, filteredRoomReserve);

        // 예약 정보 업데이트
        updateRoomReserveInfo(filteredRoomReserve, roomReserveUpdateRequestDto);

        // 수정 요청받은 시간 정보가 기존 예약과 겹치는지 판별
        checkReservationOverlap(room.getRoomReservations(), filteredRoomReserve);

        if (useRedis) {
            // Redis 분산락을 사용하여 수정
            try {
                // 락을 시도하여 획득
                if (lock.tryLock(3, TimeUnit.SECONDS)) {
                    try {
                        RoomReserve savedRoomReserve = roomReserveRepository.save(filteredRoomReserve);
                        return new RoomReserveUpdateResponseDto(savedRoomReserve);
                    } catch (OptimisticLockingFailureException e) {
                        throw new OptimisticLockConflictException();
                    }
                } else {
                    throw new ReservationLockTimeOutException();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 중단된 스레드 복원
                throw new RoomReserveException();
            }
        } else {
            // 낙관적 락만 사용하는 경우
            try {
                RoomReserve savedRoomReserve = roomReserveRepository.save(filteredRoomReserve);
                return new RoomReserveUpdateResponseDto(savedRoomReserve);
            } catch (OptimisticLockingFailureException e) {
                throw new OptimisticLockConflictException();
            }
        }
    }

    @Transactional
    public RoomReserveDeleteResponseDto deleteRoomReserve(User user, Long roomId, Long reserveId) {
        // 요청자 권한 확인 - 멤버쉽 권한이 아니면 예외처리.
        //validateUserMembership(user);

        // 예약 정보 확인.
        Room room = roomService.findRoomById(roomId);

        RoomReserve filteredRoomReserve = room.getRoomReservations().stream()
                .filter(reserve -> reserve.getId().equals(reserveId))
                .findFirst().orElseThrow(NotFoundRoomReserveException::new);

        // 스터디룸 예약을 했던 유저만이 스터디룸 예약을 삭제 할 수 있다.
        if (!filteredRoomReserve.getUser().getId().equals(user.getId())) {
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
    public Page<RoomReserveResponseDto> findAllRoomReserveByUser(int page, int size, User user, Long roomId) {
        Pageable pageable = PageRequest.of(page - 1, size);  // 페이지는 0부터 시작
        Long userId = user.getId();

        Page<RoomReserve> roomReservePage = roomReserveRepository.findAllByUserIdAndRoomId(userId, roomId, pageable);

        return roomReservePage.map(RoomReserveResponseDto::new);

    }

    /*
         예약 시간 중복 체크 (기존 예약과 비교하여)
         RoomReserveCreateRequestDto의 reservationDate와 reservationDateEnd가 기존의 예약과 겹치는지 유무 판별.
         예약이 겹치는 Case
         Case 1: 새로운 예약의 reservationDate가 기존 예약 사이에 있는 경우
         Case 2: 새로운 예약의 reservationDateEnd가 기존 예약 사이에 있는 경우
         Case 3: 새로운 예약이 기존 예약을 덮거나, 기존 예약이 새로운 예약을 덮는 경우.
         */
    private void checkReserveOverlap(Long roomId, LocalDateTime reservationDate, LocalDateTime reservationDateEnd) {
        List<RoomReserve> roomReserveList = roomReserveRepository.findAllByRoomId(roomId);

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
    }

    private RoomReserve findRoomReserveById(Room room, Long reserveId) {
        return room.getRoomReservations().stream()
                .filter(reserve -> reserve.getId().equals(reserveId))
                .findFirst()
                .orElseThrow(NotFoundRoomReserveException::new);
    }

    private void validateUserReservation(User user, RoomReserve filteredRoomReserve) {
        if (!filteredRoomReserve.getUser().getId().equals(user.getId())) {
            throw new ReservationModificationNotAllowedException();
        }
    }

    private void updateRoomReserveInfo(RoomReserve filteredRoomReserve, RoomReserveUpdateRequestDto roomReserveUpdateRequestDto) {
        if (roomReserveUpdateRequestDto.getReservationDate() != null) {
            filteredRoomReserve.updateReservationDate(roomReserveUpdateRequestDto.getReservationDate());
        }
        if (roomReserveUpdateRequestDto.getReservationDateEnd() != null) {
            filteredRoomReserve.updateReservationDateEnd(roomReserveUpdateRequestDto.getReservationDateEnd());
        }
    }

    private void checkReservationOverlap(List<RoomReserve> roomReserveList, RoomReserve filteredRoomReserve) {
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
    }

    private RoomReserveCreateResponseDto processRoomReservation(User user, Long roomId, RoomReserveCreateRequestDto roomReserveCreateRequestDto) {
        Room room = roomService.findRoomById(roomId);

        if (room.getRoomStatus() == RoomStatus.NON_AVAILABLE) {
            throw new RoomReserveUnavailableException();
        }

        checkReserveOverlap(roomId, roomReserveCreateRequestDto.getReservationDate(), roomReserveCreateRequestDto.getReservationDateEnd());
        RoomReserve roomReserve = RoomReserve.createReservation(room, user, roomReserveCreateRequestDto);

        try {
            // 예약 내용 저장 (낙관적 락 방식)
            RoomReserve savedRoomReserve = roomReserveRepository.save(roomReserve);
            return new RoomReserveCreateResponseDto(savedRoomReserve);
        } catch (OptimisticLockingFailureException e) {
            throw new OptimisticLockConflictException();
        }
    }

    // 멤버쉽 보유 체크
    private void validateUserMembership(User user) {
        if (user.getMembership() == null) {
            throw new MembershipNotFoundException();
        }

        MembershipStatus status = user.getMembership().getStatus();

        switch (status) {
            case ACTIVE:
                log.info("User ID {} has an active membership.", user.getId());
                break;
            case EXPIRED:
                throw new MembershipExpiredException();
            case CANCELLED:
                throw new MembershipCancelledException();
            default:
                throw new MembershipNotFoundException();
        }
    }
}
