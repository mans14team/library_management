package com.example.library_management.domain.roomReserveBackup.entity;

import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "room_reserve_backup")
public class RoomReserveBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime reservationDate;

    @Column(nullable = false)
    private LocalDateTime reservationDateEnd;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long roomId;

    // 백업 시점 저장
    @Column(nullable = false)
    private LocalDateTime backupTimestamp;

    public RoomReserveBackup() {}

    public static RoomReserveBackup from(RoomReserve roomReserve) {
        RoomReserveBackup backup = new RoomReserveBackup();
        //backup.id = roomReserve.getId();
        backup.reservationDate = roomReserve.getReservationDate();
        backup.reservationDateEnd = roomReserve.getReservationDateEnd();
        backup.userId = roomReserve.getUser().getId();
        backup.roomId = roomReserve.getRoom().getId();
        backup.backupTimestamp = LocalDateTime.now();
        return backup;
    }
}
