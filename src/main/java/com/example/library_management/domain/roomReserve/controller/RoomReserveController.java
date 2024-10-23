package com.example.library_management.domain.roomReserve.controller;

import com.example.library_management.domain.roomReserve.dto.response.RoomReserveCreateResponseDto;
import com.example.library_management.domain.roomReserve.service.RoomReserveService;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoomReserveController {

    private final RoomReserveService roomReserveService;

    @PostMapping("/library/rooms/{roomId}/reservations")
    public ResponseEntity<RoomReserveCreateResponseDto> createRoomReserve(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long roomId){
        return ResponseEntity.ok(roomReserveService.createRoomReserve(userDetails, roomId));
    }
}
