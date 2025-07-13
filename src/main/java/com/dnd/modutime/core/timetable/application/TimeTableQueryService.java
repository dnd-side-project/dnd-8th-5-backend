package com.dnd.modutime.core.timetable.application;

import com.dnd.modutime.core.timetable.domain.TimeTable;
import com.dnd.modutime.core.timetable.repository.TimeTableRepository;
import com.dnd.modutime.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimeTableQueryService {

    private final TimeTableRepository timeTableRepository;

    public TimeTable findByRoomUuid(String roomUuid) {
        return timeTableRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 TimeTable을 찾을 수 없습니다."));
    }


}
