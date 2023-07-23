package com.dnd.modutime.core.room.application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.domain.RoomDate;
import com.dnd.modutime.core.room.repository.RoomRepository;
import com.dnd.modutime.core.timetable.application.TimeTableInitializer;
import com.dnd.modutime.core.timetable.domain.DateInfo;
import com.dnd.modutime.core.timetable.domain.TimeInfo;
import com.dnd.modutime.core.timetable.domain.TimeTable;
import com.dnd.modutime.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoomTimeTableInitializer implements TimeTableInitializer {

    private static final int INITIAL_TIME_INFOS_CAPACITY = 50;
    private static final LocalTime ZERO_TIME = LocalTime.of(0, 0);

    private final RoomRepository roomRepository;

    @Override
    public void initialize(String roomUuid, TimeTable timeTable) {
        Room room = getRoomByRoomUuid(roomUuid);
        List<LocalDate> dates = room.getRoomDates().stream()
                .map(RoomDate::getDate)
                .collect(Collectors.toList());
        List<DateInfo> dateInfos = new ArrayList<>(dates.size());

        for (LocalDate date : dates) {
            List<TimeInfo> timeInfos = new ArrayList<>(INITIAL_TIME_INFOS_CAPACITY);
            addTimeInfos(room, timeInfos);
            DateInfo dateInfo = new DateInfo(timeTable, date, timeInfos);
            dateInfos.add(dateInfo);
        }
        timeTable.replaceDateInfos(dateInfos);
    }

    private void addTimeInfos(Room room,
                              List<TimeInfo> timeInfos) {
        LocalTime startTime = room.getStartTimeOrNull();
        LocalTime endTime = room.getEndTimeOrNull();
        if (!room.hasStartAndEndTime()) {
            timeInfos.add(new TimeInfo(null, new ArrayList<>()));
            return;
        }

        if (startTime.isAfter(endTime)) {
            addTimeInfosWhenStartTimeIsAfterEndTime(timeInfos, startTime, endTime);
            return;
        }
        for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(30)) {
            timeInfos.add(new TimeInfo(time, new ArrayList<>()));
        }
    }

    private static void addTimeInfosWhenStartTimeIsAfterEndTime(List<TimeInfo> timeInfos, LocalTime startTime, LocalTime endTime) {

        for (LocalTime time = ZERO_TIME; time.isBefore(endTime); time = time.plusMinutes(30)) {
            timeInfos.add(new TimeInfo(time, new ArrayList<>()));
        }

        for (LocalTime time = startTime; !time.equals(ZERO_TIME); time = time.plusMinutes(30)) {
            timeInfos.add(new TimeInfo(time, new ArrayList<>()));
        }
    }

    private Room getRoomByRoomUuid(String roomUuid) {
        return roomRepository.findByUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 방이 없습니다."));
    }
}
