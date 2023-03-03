package com.dnd.modutime.fixture;

import static com.dnd.modutime.fixture.RoomRequestFixture.ROOM_UUID;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_10;

import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.AvailableTime;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import com.dnd.modutime.core.timetable.domain.DateInfo;
import com.dnd.modutime.core.timetable.domain.TimeInfo;
import com.dnd.modutime.core.timetable.domain.TimeTable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TimeTableFixture {

    public static DateInfo getDateInfo(List<TimeInfo> timeInfos) {
        return getDateInfo(_2023_02_10, timeInfos);
    }

    public static DateInfo getDateInfo(LocalDate date, List<TimeInfo> timeInfos) {
        return new DateInfo(getTimeTable(), date, timeInfos);
    }

    public static TimeTable getTimeTable() {
        return new TimeTable(ROOM_UUID);
    }

    public static TimeInfo getTimeInfo(LocalTime time) {
        return new TimeInfo(time, new ArrayList<>());
    }

    public static AvailableDateTime getAvailableDateTime(String participantName, LocalDate date, List<AvailableTime> availableTimes) {
        return new AvailableDateTime(new TimeBlock(ROOM_UUID, participantName), date, availableTimes);
    }
}
