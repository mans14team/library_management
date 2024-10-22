package com.example.library_management.domain.room.controller;

import com.example.library_management.domain.room.dto.request.RoomCreateRequestDto;
import com.example.library_management.domain.room.dto.response.RoomCreateResponseDto;
import com.example.library_management.domain.room.dto.request.RoomUpdateRequestDto;
import com.example.library_management.domain.room.dto.response.RoomDeleteResponseDto;
import com.example.library_management.domain.room.dto.response.RoomUpdateResponseDto;
import com.example.library_management.domain.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping("/library/rooms")
    public ResponseEntity<RoomCreateResponseDto> createRoom(@RequestBody RoomCreateRequestDto roomCreateRequestDto) {
        return ResponseEntity.ok(roomService.createRoom(roomCreateRequestDto));
    }

    @PatchMapping("/library/rooms/{roomId}")
    public ResponseEntity<RoomUpdateResponseDto> updateRoom(@PathVariable Long roomId, @RequestBody RoomUpdateRequestDto roomUpdateRequestDto) {
        return ResponseEntity.ok(roomService.updateRoom(roomId, roomUpdateRequestDto));
    }

    @DeleteMapping("/library/rooms/{roomId}")
    public ResponseEntity<RoomDeleteResponseDto> deleteRoom(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.deleteRoom(roomId));
    }

}
