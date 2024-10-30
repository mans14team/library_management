package com.example.library_management.domain.roomReserve.repository;

import com.example.library_management.domain.review.entity.Review;
import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomReserveRepository extends JpaRepository<RoomReserve, Long> {

    // 스터디룸 예약 - 동시성 제어 로직 (Pessimistic Lock)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM RoomReserve r WHERE r.room.id = :roomId")
    List<RoomReserve> findAllByRoomIdWithLock(@Param("roomId") Long roomId);

    @Query("SELECT r FROM RoomReserve r WHERE r.user.id = :userId AND r.room.id = :roomId")
    Page<RoomReserve> findAllByUserIdAndRoomId(@Param("userId") Long userId, @Param("roomId") Long roomId, Pageable pageable);

    @Query("SELECT r from RoomReserve r WHERE r.room.id = :roomId")
    Page<RoomReserve> findAllByRoomId(@Param("roomId") Long roomId, Pageable pageable);
}
