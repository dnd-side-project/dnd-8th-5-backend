package com.dnd.modutime.timetable.application;

import com.dnd.modutime.timetable.domain.TimeTable;

public interface TimeTableInitializer {

    void initialize(String roomUuid, final TimeTable timeTable);
}
