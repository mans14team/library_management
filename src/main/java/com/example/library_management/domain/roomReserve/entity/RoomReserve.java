package com.example.library_management.domain.roomReserve.entity;

import com.example.library_management.domain.common.entity.Timestamped;
import com.example.library_management.domain.room.entity.Room;
import com.example.library_management.domain.roomReserve.dto.request.RoomReserveCreateRequestDto;
import com.example.library_management.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Getter
@ToString
@Table(name = "room_reserve")
public class RoomReserve extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime reservationDate;

    @Column(nullable = false)
    private LocalDateTime reservationDateEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    // 낙관적 락 적용, RoomReserve 엔티티의 변경을 감지하여 버전충돌을 확인. -> 충돌시 OptimisticLockException 발생 테스트코드에서 활용.
    @Version
    private Integer version;

    public static RoomReserve createReservation(Room room, User user, RoomReserveCreateRequestDto roomReserveCreateRequestDto) {
        RoomReserve roomReserve = new RoomReserve();
        roomReserve.room = room;
        roomReserve.user = user;
        roomReserve.reservationDate = roomReserveCreateRequestDto.getReservationDate();
        roomReserve.reservationDateEnd = roomReserveCreateRequestDto.getReservationDateEnd();
        return roomReserve;
    }

    public void updateReservationDate(LocalDateTime reservationDate) {
        this.reservationDate = reservationDate;
    }

    public void updateReservationDateEnd(LocalDateTime reservationDateEnd) {
        this.reservationDateEnd = reservationDateEnd;
    }

}
