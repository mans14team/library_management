package com.example.library_management.domain.roomReserve.entity;

import com.example.library_management.domain.common.entity.Timestamped;
import com.example.library_management.domain.room.entity.Room;
import com.example.library_management.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
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
}
