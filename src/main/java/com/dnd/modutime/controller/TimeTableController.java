package com.dnd.modutime.controller;


import com.dnd.modutime.application.TimeTableService;
import com.dnd.modutime.dto.request.TimeReplaceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
