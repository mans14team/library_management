package com.example.library_management.domain.room.entity;

import com.example.library_management.domain.room.enums.RoomStatus;
import com.example.library_management.domain.roomReserve.entity.RoomReserve;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "room")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomName;

    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    @OneToMany(mappedBy = "room")
    private List<RoomReserve> roomReservations = new ArrayList<>();
}
