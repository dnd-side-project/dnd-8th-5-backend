package com.dnd.modutime.application;

import com.dnd.modutime.domain.room.Room;
import com.dnd.modutime.domain.room.RoomDate;
import com.dnd.modutime.domain.timetable.DateInfo;
import com.dnd.modutime.domain.timetable.TimeInfo;
import com.dnd.modutime.domain.timetable.TimeTable;
import com.dnd.modutime.exception.NotFoundException;
import com.dnd.modutime.repository.RoomRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomTimeTableInitializer implements TimeTableInitializer{

    private static final int INITIAL_TIME_INFOS_CAPACITY = 50;

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
            timeInfos.add(new TimeInfo(null, 0));
            return;
        }
        for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusMinutes(30)) {
            timeInfos.add(new TimeInfo(time, 0));
        }
    }

    private Room getRoomByRoomUuid(String roomUuid) {
        return roomRepository.findByUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 방이 없습니다."));
    }
}
