package com.dnd.modutime.core.timetable.domain.view;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class TimeTableSearchCondition {
    private String roomUuid;
    private List<String> participantName;

    public static TimeTableSearchCondition of(String roomUuid, List<String> participantName) {
        var condition = new TimeTableSearchCondition();
        condition.roomUuid = roomUuid;
        condition.participantName = participantName;
        return condition;
    }
}
