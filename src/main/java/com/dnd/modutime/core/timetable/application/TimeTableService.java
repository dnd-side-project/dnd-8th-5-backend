package com.dnd.modutime.core.timetable.application;

import com.dnd.modutime.core.timetable.application.command.TimeTableUpdateCommand;
import com.dnd.modutime.core.timetable.application.response.TimeTableResponse;
import com.dnd.modutime.core.timetable.domain.TimeTable;
import com.dnd.modutime.core.timetable.domain.TimeTableReplaceEvent;
import com.dnd.modutime.core.timetable.repository.TimeInfoParticipantNameRepository;
import com.dnd.modutime.core.timetable.repository.TimeTableRepository;
import com.dnd.modutime.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeTableService {

    private final TimeTableRepository timeTableRepository;
    private final TimeTableInitializer timeTableInitializer;
    private final TimeInfoParticipantNameRepository timeInfoParticipantNameRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void create(String roomUuid) {
        TimeTable timeTable = timeTableRepository.save(new TimeTable(roomUuid));
        timeTableInitializer.initialize(roomUuid, timeTable);
    }

    public TimeTableResponse getTimeTable(String roomUuid) {
        TimeTable timeTable = getTimeTableByRoomUuid(roomUuid);
        return TimeTableResponse.from(timeTable);
    }

    private TimeTable getTimeTableByRoomUuid(String roomUuid) {
        return timeTableRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 TimeTable을 찾을 수 없습니다."));
    }

    @Transactional
    public void update(TimeTableUpdateCommand command) {
        TimeTable timeTable = getTimeTableByRoomUuid(command.getRoomUuid());
        List<Long> timeInfoIds = timeTable.getTimeInfoIdsByAvailableDateTimes(command.getOldAvailableDateTimes());

        timeTable.removeParticipantName(command.getOldAvailableDateTimes(), command.getParticipantName());
        for (Long timeInfoId : timeInfoIds) {
            timeInfoParticipantNameRepository.deleteByTimeInfoIdAndName(timeInfoId, command.getParticipantName());
        }

        timeTable.addParticipantName(command.getNewAvailableDateTimes(), command.getParticipantName());
        timeTableRepository.save(timeTable);
        eventPublisher.publishEvent(new TimeTableReplaceEvent(command.getRoomUuid(), timeTable.getDateInfos()));
    }
}
