package com.dnd.modutime.core.room.controller;

import com.dnd.modutime.core.adjustresult.application.AdjustmentResultService;
import com.dnd.modutime.core.room.application.RoomService;
import com.dnd.modutime.core.room.application.request.RoomRequest;
import com.dnd.modutime.core.room.application.response.RoomCreationResponse;
import com.dnd.modutime.core.room.application.response.V2RoomInfoResponse;
import com.dnd.modutime.core.timetable.application.TimeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RoomGuestController {

    private final RoomService roomService;
    private final TimeTableService timeTableService;
    private final AdjustmentResultService adjustmentResultService;

    @PostMapping("/guest/api/room")
    public ResponseEntity<RoomCreationResponse> create(@RequestBody RoomRequest roomRequest) {
        RoomCreationResponse roomCreationResponse = roomService.create(roomRequest);
        timeTableService.create(roomCreationResponse.getUuid());
        adjustmentResultService.create(roomCreationResponse.getUuid());

        return ResponseEntity.ok(roomCreationResponse);
    }

    @GetMapping("/guest/api/v2/room/{roomUuid}")
    public ResponseEntity<V2RoomInfoResponse> v2getInfo(@PathVariable String roomUuid) {
        var roomInfoResponse = roomService.v2getInfo(roomUuid);
        return ResponseEntity.ok(roomInfoResponse);
    }
}
