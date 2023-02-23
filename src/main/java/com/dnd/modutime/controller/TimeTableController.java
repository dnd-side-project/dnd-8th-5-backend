package com.dnd.modutime.controller;


import com.dnd.modutime.application.TimeTableService;
import com.dnd.modutime.dto.request.TimeReplaceRequest;
import com.dnd.modutime.dto.response.TimeBlockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room/{roomUuid}/available-time")
@RequiredArgsConstructor
public class TimeTableController {

    private final TimeTableService timeTableService;

    @PutMapping
    public ResponseEntity<Void> replace(@PathVariable String roomUuid,
                                        @RequestBody TimeReplaceRequest timeReplaceRequest) {
        timeTableService.replace(roomUuid, timeReplaceRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<TimeBlockResponse> getTimeBlock(@PathVariable String roomUuid,
                                                          @RequestParam String name) {
        TimeBlockResponse timeBlockResponse = timeTableService.getTimeBlock(roomUuid, name);
        return ResponseEntity.ok(timeBlockResponse);
    }
}
