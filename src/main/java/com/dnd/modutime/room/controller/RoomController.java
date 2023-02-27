package com.dnd.modutime.room.controller;

import com.dnd.modutime.room.application.RoomService;
import com.dnd.modutime.dto.request.RoomRequest;
import com.dnd.modutime.dto.response.RoomCreationResponse;
import com.dnd.modutime.dto.response.RoomInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomCreationResponse> create(@RequestBody RoomRequest roomRequest) {
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);
        return ResponseEntity.ok(roomCreationResponse);
    }

    @GetMapping("/{roomUuid}")
    public ResponseEntity<RoomInfoResponse> getInfo(@PathVariable String roomUuid) {
        RoomInfoResponse roomInfoResponse = roomService.getInfo(roomUuid);
        return ResponseEntity.ok(roomInfoResponse);
    }
}
