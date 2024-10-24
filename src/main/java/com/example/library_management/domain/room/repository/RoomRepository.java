package com.example.library_management.domain.room.repository;

import com.example.library_management.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsById(Long id);

}
