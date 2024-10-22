package com.example.library_management.domain.room.entity;

import com.example.library_management.domain.room.dto.request.RoomCreateRequestDto;
import com.example.library_management.domain.room.dto.request.RoomUpdateRequestDto;
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

    // 해당 Room에 대한 예약 정보 List
    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RoomReserve> roomReservations = new ArrayList<>();

    public static Room createRoom(RoomCreateRequestDto roomCreateRequestDto) {
        Room room = new Room();
        room.roomName = roomCreateRequestDto.getRoomName();
        room.roomStatus = roomCreateRequestDto.getRoomStatus();
        return room;
    }

    // 스터디룸 수정
    public void update(RoomUpdateRequestDto roomUpdateRequestDto) {
        this.roomName = roomUpdateRequestDto.getRoomName();
        this.roomStatus = roomUpdateRequestDto.getRoomStatus();
    }
}
