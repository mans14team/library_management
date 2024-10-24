package com.example.library_management.domain.room.controller;

import com.example.library_management.domain.room.dto.request.RoomCreateRequestDto;
import com.example.library_management.domain.room.dto.response.RoomCreateResponseDto;
import com.example.library_management.domain.room.dto.request.RoomUpdateRequestDto;
import com.example.library_management.domain.room.dto.response.RoomDeleteResponseDto;
import com.example.library_management.domain.room.dto.response.RoomGetResponseDto;
import com.example.library_management.domain.room.dto.response.RoomUpdateResponseDto;
import com.example.library_management.domain.room.service.RoomService;
import com.example.library_management.global.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/library/rooms/{roomId}")
    public ResponseEntity<RoomGetResponseDto> getRoom(@PathVariable Long roomId){
        return ResponseEntity.ok(roomService.getRoom(roomId));
    }

    @PostMapping("/library/rooms")
    public ResponseEntity<RoomCreateResponseDto> createRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @Valid @RequestBody RoomCreateRequestDto roomCreateRequestDto) {
        return ResponseEntity.ok(roomService.createRoom(userDetails, roomCreateRequestDto));
    }

    @PatchMapping("/library/rooms/{roomId}")
    public ResponseEntity<RoomUpdateResponseDto> updateRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long roomId, @RequestBody RoomUpdateRequestDto roomUpdateRequestDto) {
        return ResponseEntity.ok(roomService.updateRoom(userDetails, roomId, roomUpdateRequestDto));
    }

    @DeleteMapping("/library/rooms/{roomId}")
    public ResponseEntity<RoomDeleteResponseDto> deleteRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long roomId) {
        return ResponseEntity.ok(roomService.deleteRoom(userDetails, roomId));
    }

}
