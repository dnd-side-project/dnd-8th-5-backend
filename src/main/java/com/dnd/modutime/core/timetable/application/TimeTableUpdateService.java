package com.dnd.modutime.core.timetable.application;

import com.dnd.modutime.core.timetable.domain.TimeTableReplaceEvent;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import com.dnd.modutime.core.timeblock.domain.TimeBlockReplaceEvent;
import com.dnd.modutime.core.timetable.domain.TimeTable;
import com.dnd.modutime.core.timetable.repository.TimeInfoParticipantNameRepository;
import com.dnd.modutime.core.timetable.repository.TimeTableRepository;
import com.dnd.modutime.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimeTableUpdateService {

    private final TimeTableRepository timeTableRepository;
    private final TimeInfoParticipantNameRepository timeInfoParticipantNameRepository;
    private final ApplicationEventPublisher eventPublisher;

    // TODO: test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void update(TimeBlockReplaceEvent event) {
        TimeTable timeTable = getTimeTableByRoomUuid(event.getRoomUuid());
        List<Long> timeInfoIds = timeTable.getTimeInfoIdsByAvailableDateTimes(event.getOldAvailableDateTimes());

        timeTable.removeParticipantName(event.getOldAvailableDateTimes(), event.getParticipantName());
        for (Long timeInfoId : timeInfoIds) {
            timeInfoParticipantNameRepository.deleteByTimeInfoIdAndName(timeInfoId, event.getParticipantName());
        }

        timeTable.addParticipantName(event.getNewAvailableDateTimes(), event.getParticipantName());
        timeTableRepository.save(timeTable);
        eventPublisher.publishEvent(new TimeTableReplaceEvent(event.getRoomUuid(), timeTable.getDateInfos()));
    }

    private TimeTable getTimeTableByRoomUuid(String roomUuid) {
        return timeTableRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 TimeTable을 찾을 수 없습니다."));
    }
}
