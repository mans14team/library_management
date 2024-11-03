package com.example.library_management.domain.roomReserve.repository;

import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomReserveRepository extends JpaRepository<RoomReserve, Long> {

    @Modifying
    @Query("DELETE FROM RoomReserve r WHERE r.reservationDateEnd < :endTime")
    void deleteByEndTimeBefore(@Param("endTime") LocalDateTime endTime);

    @Query("SELECT r FROM RoomReserve r WHERE r.room.id = :roomId")
    List<RoomReserve> findAllByRoomId(@Param("roomId") Long roomId);

    @Query("SELECT r FROM RoomReserve r WHERE r.user.id = :userId AND r.room.id = :roomId")
    Page<RoomReserve> findAllByUserIdAndRoomId(@Param("userId") Long userId, @Param("roomId") Long roomId, Pageable pageable);

    @Query("SELECT r from RoomReserve r WHERE r.room.id = :roomId")
    Page<RoomReserve> findAllByRoomId(@Param("roomId") Long roomId, Pageable pageable);

    @Query("select r from RoomReserve r join  fetch  r.user join fetch r.room " +
            "where r.reservationDateEnd between :rsDate_Start and :rsDate_End ")
    List<RoomReserve> findReservation(@Param("rsDate_Start") LocalDateTime startDate, @Param("rsDate_End") LocalDateTime endDate);
}
