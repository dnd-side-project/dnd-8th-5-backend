package com.dnd.modutime.timetable.application;

import com.dnd.modutime.timetable.domain.TimeTable;
import com.dnd.modutime.dto.response.TimeTableResponse;
import com.dnd.modutime.exception.NotFoundException;
import com.dnd.modutime.timetable.repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimeTableService {

    private final TimeTableRepository timeTableRepository;

    public TimeTableResponse getTimeTable(String roomUuid) {
        TimeTable timeTable = getTimeTableByRoomUuid(roomUuid);
        return TimeTableResponse.from(timeTable);
    }

    private TimeTable getTimeTableByRoomUuid(String roomUuid) {
        return timeTableRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 TimeBlock을 찾을 수 없습니다."));
    }
}
