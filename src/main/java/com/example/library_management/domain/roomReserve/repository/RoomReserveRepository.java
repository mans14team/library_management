package com.example.library_management.domain.roomReserve.repository;

import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RoomReserveRepository extends JpaRepository<RoomReserve, Long> {

    @Query("select r from RoomReserve r where r.reservationDateEnd=:reservationDate ")
    List<RoomReserve> findReservation(@Param("reservationDate") LocalDateTime targetDate);
}
