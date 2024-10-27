package com.example.library_management.domain.roomReserve.controller;

import com.example.library_management.domain.roomReserve.dto.request.RoomReserveCreateRequestDto;
import com.example.library_management.domain.roomReserve.dto.request.RoomReserveUpdateRequestDto;
import com.example.library_management.domain.roomReserve.dto.response.RoomReserveCreateResponseDto;
import com.example.library_management.domain.roomReserve.dto.response.RoomReserveDeleteResponseDto;
import com.example.library_management.domain.roomReserve.dto.response.RoomReserveResponseDto;
import com.example.library_management.domain.roomReserve.dto.response.RoomReserveUpdateResponseDto;
import com.example.library_management.domain.roomReserve.service.RoomReserveService;
import com.example.library_management.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RoomReserveController {

    private final RoomReserveService roomReserveService;

    @PostMapping("/library/rooms/{roomId}/reservations")
    public ResponseEntity<RoomReserveCreateResponseDto> createRoomReserve(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                          @PathVariable Long roomId,
                                                                          @RequestBody RoomReserveCreateRequestDto roomReserveCreateRequestDto) {
        return ResponseEntity.ok(roomReserveService.createRoomReserve(userDetails.getUser(), roomId, roomReserveCreateRequestDto));
    }

    @PatchMapping("/library/rooms/{roomId}/reservations/{reserveId}")
    public ResponseEntity<RoomReserveUpdateResponseDto> updateRoomReserve(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                          @PathVariable Long roomId,
                                                                          @PathVariable Long reserveId,
                                                                          @RequestBody RoomReserveUpdateRequestDto roomReserveUpdateRequestDto) {
        // (예약 시작일, 예약 종료일) 둘 중 최소 하나 이상의 데이터의 전달 유무 검증.
        roomReserveUpdateRequestDto.validate();

        return ResponseEntity.ok(roomReserveService.updateRoomReserve(userDetails.getUser(), roomId, reserveId, roomReserveUpdateRequestDto));
    }

    @DeleteMapping("/library/rooms/{roomId}/reservations/{reserveId}")
    public ResponseEntity<RoomReserveDeleteResponseDto> deleteRoomReserve(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                          @PathVariable Long roomId,
                                                                          @PathVariable Long reserveId) {
        return ResponseEntity.ok(roomReserveService.deleteRoomReserve(userDetails.getUser(), roomId, reserveId));
    }

    @GetMapping("/library/rooms/{roomId}/reservations")
    public ResponseEntity<Page<RoomReserveResponseDto>> findAllRoomReserve(@RequestParam(defaultValue = "1") int page,
                                                                           @RequestParam(defaultValue = "10") int size,
                                                                           @PathVariable Long roomId) {
        return ResponseEntity.ok(roomReserveService.findAllRoomReserve(page, size, roomId));
    }

    @GetMapping("/library/rooms/{roomId}/reservations/users")
    public ResponseEntity<Page<RoomReserveResponseDto>> findAllRoomReserveByUser(@RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int size,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                                      @PathVariable Long roomId) {
        return ResponseEntity.ok(roomReserveService.findAllRoomReserveByUser(page, size, userDetails.getUser(), roomId));
    }
}
