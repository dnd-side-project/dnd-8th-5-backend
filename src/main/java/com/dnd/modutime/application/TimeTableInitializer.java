package com.dnd.modutime.application;

import com.dnd.modutime.domain.timetable.TimeTable;

public interface TimeTableInitializer {

    void initialize(String roomUuid, final TimeTable timeTable);
}
