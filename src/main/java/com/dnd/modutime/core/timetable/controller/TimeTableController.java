package com.dnd.modutime.core.timetable.controller;


import com.dnd.modutime.core.timetable.application.TimeTableService;
import com.dnd.modutime.core.timetable.application.response.TimeTableResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room/{roomUuid}/available-time")
@RequiredArgsConstructor
public class TimeTableController {

    private final TimeTableService timeTableService;

    @GetMapping("/group")
    public ResponseEntity<TimeTableResponse> getTimeTable(@PathVariable String roomUuid) {
        TimeTableResponse timeTableResponse = timeTableService.getTimeTable(roomUuid);
        return ResponseEntity.ok(timeTableResponse);
    }
}
