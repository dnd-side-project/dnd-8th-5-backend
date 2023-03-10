package com.dnd.modutime.core.timetable.application;

import com.dnd.modutime.core.timetable.domain.TimeTable;
import com.dnd.modutime.core.timetable.repository.TimeInfoParticipantNameRepository;
import com.dnd.modutime.core.timetable.repository.TimeTableRepository;
import com.dnd.modutime.exception.NotFoundException;
import com.dnd.modutime.core.timeblock.domain.TimeBlockReplaceEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class TimeTableUpdateService {

    private final TimeTableRepository timeTableRepository;
    private final TimeInfoParticipantNameRepository timeInfoParticipantNameRepository;

    // TODO: test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void update(TimeBlockReplaceEvent event) {
        TimeTable timeTable = getTimeTableByRoomUuid(event.getRoomUuid());
        List<Long> timeInfoIds = timeTable.getTimeInfoIdsByAvailableDateTimes(event.getOldAvailableDateTimes());
        for (Long timeInfoId : timeInfoIds) {
            timeInfoParticipantNameRepository.deleteByTimeInfoIdAndName(timeInfoId, event.getParticipantName());
        }
        timeTable.updateParticipantName(event.getOldAvailableDateTimes(),
                event.getNewAvailableDateTimes(),
                event.getParticipantName());
        timeTableRepository.save(timeTable);
    }

    private TimeTable getTimeTableByRoomUuid(String roomUuid) {
        return timeTableRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 TimeTable을 찾을 수 없습니다."));
    }
}
