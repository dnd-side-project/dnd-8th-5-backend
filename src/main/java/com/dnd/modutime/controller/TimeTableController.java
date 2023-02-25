package com.dnd.modutime.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room/{roomUuid}/available-time")
@RequiredArgsConstructor
public class TimeTableController {

//    private final TimeTableService timeTableService;
//
//    @GetMapping("/group")
//    public ResponseEntity<TimeTableResponse> getTimeTable(@PathVariable String roomUuid) {
//        TimeTableResponse timeTableResponse = timeTableService.getTimeTable(roomUuid);
//        return ResponseEntity.ok(timeTableResponse);
//    }
}
