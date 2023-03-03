package com.dnd.modutime.core.room.controller;

import com.dnd.modutime.core.adjustresult.application.AdjustmentResultService;
import com.dnd.modutime.core.room.application.RoomService;
import com.dnd.modutime.core.room.application.request.RoomRequest;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.core.room.application.response.RoomInfoResponse;
import com.dnd.modutime.core.timetable.application.TimeTableService;
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
    private final TimeTableService timeTableService;
    private final AdjustmentResultService adjustmentResultService;

    @PostMapping
    public ResponseEntity<RoomCreationResponse> create(@RequestBody RoomRequest roomRequest) {
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);
        timeTableService.create(roomCreationResponse.getUuid());
        adjustmentResultService.create(roomCreationResponse.getUuid());

        return ResponseEntity.ok(roomCreationResponse);
    }

    @GetMapping("/{roomUuid}")
    public ResponseEntity<RoomInfoResponse> getInfo(@PathVariable String roomUuid) {
        RoomInfoResponse roomInfoResponse = roomService.getInfo(roomUuid);
        return ResponseEntity.ok(roomInfoResponse);
    }
}
