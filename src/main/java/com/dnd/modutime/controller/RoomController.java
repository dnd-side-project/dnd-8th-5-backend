package com.dnd.modutime.controller;

import com.dnd.modutime.application.RoomService;
import com.dnd.modutime.dto.RoomRequest;
import com.dnd.modutime.dto.RoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<RoomResponse> create(@RequestBody RoomRequest roomRequest) {
        RoomResponse roomResponse = roomService.create(roomRequest);
        return ResponseEntity.ok(roomResponse);
    }
}
