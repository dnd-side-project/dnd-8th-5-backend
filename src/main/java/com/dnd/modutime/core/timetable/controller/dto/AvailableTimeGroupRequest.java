package com.dnd.modutime.core.timetable.controller.dto;

import com.dnd.modutime.core.timetable.domain.view.TimeTableSearchCondition;

import java.util.List;

public record AvailableTimeGroupRequest(
        List<String> participantNames
) {
    public TimeTableSearchCondition toCondition(String roomUuid) {
        return TimeTableSearchCondition.of(
                roomUuid,
                this.participantNames
        );
    }
}
