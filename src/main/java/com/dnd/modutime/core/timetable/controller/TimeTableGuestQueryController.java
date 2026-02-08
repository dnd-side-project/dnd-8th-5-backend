package com.dnd.modutime.core.timetable.controller;

import com.dnd.modutime.core.timetable.application.TimeTableFacade;
import com.dnd.modutime.core.timetable.controller.dto.AvailableTimeGroupRequest;
import com.dnd.modutime.core.timetable.domain.view.TimeTableOverview;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class TimeTableGuestQueryController {

    private final TimeTableFacade facade;

    @GetMapping("/guest/api/room/{roomUuid}/available-time/overview")
    public ResponseEntity<TimeTableOverview> v2getOverview(@PathVariable String roomUuid,
                                                           @Valid AvailableTimeGroupRequest request) {
        var timeTableResponse = facade.getOverview(request.toCondition(roomUuid));
        return ResponseEntity.ok(timeTableResponse);
    }
}
