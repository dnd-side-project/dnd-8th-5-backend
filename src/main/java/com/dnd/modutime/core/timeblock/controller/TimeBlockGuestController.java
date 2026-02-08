package com.dnd.modutime.core.timeblock.controller;


import com.dnd.modutime.core.timeblock.application.TimeBlockService;
import com.dnd.modutime.core.timeblock.application.request.TimeReplaceRequest;
import com.dnd.modutime.core.timeblock.application.response.TimeBlockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TimeBlockGuestController {

    private final TimeBlockService timeBlockService;

    @GetMapping("/guest/api/room/{roomUuid}/available-time")
    public ResponseEntity<TimeBlockResponse> getTimeBlock(@PathVariable String roomUuid,
                                                          @RequestParam String name) {
        TimeBlockResponse timeBlockResponse = timeBlockService.getTimeBlock(roomUuid, name);
        return ResponseEntity.ok(timeBlockResponse);
    }
}
