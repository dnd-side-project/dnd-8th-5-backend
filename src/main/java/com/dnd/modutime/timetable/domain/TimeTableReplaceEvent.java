package com.dnd.modutime.timetable.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimeTableReplaceEvent {

    private final String roomUuid;
    private final List<DateInfo> dateInfos;
}
