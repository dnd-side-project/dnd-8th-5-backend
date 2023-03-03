package com.dnd.modutime.core.timetable.application;

import com.dnd.modutime.core.timetable.domain.TimeTable;

public interface TimeTableInitializer {

    void initialize(String roomUuid, final TimeTable timeTable);
}
