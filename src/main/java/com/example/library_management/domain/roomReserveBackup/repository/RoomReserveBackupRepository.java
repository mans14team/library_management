package com.example.library_management.domain.roomReserveBackup.repository;

import com.example.library_management.domain.roomReserveBackup.entity.RoomReserveBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomReserveBackupRepository extends JpaRepository<RoomReserveBackup, Long> {
}
