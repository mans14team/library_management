package com.example.library_management.domain.roomReserve.repository;

import com.example.library_management.domain.review.entity.Review;
import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomReserveRepository extends JpaRepository<RoomReserve, Long> {

    @Query("SELECT r FROM RoomReserve r WHERE r.user.id = :userId AND r.room.id = :roomId")
    Page<RoomReserve> findAllByUserIdAndRoomId(@Param("userId") Long userId, @Param("roomId") Long roomId, Pageable pageable);

    @Query("SELECT r from RoomReserve r WHERE r.room.id = :roomId")
    Page<RoomReserve> findAllByRoomId(@Param("roomId") Long roomId, Pageable pageable);
}
