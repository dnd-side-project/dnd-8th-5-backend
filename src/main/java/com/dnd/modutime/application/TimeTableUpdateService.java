package com.dnd.modutime.application;

import com.dnd.modutime.domain.timeblock.TimeBlockReplaceEvent;
import com.dnd.modutime.domain.timetable.TimeTable;
import com.dnd.modutime.exception.NotFoundException;
import com.dnd.modutime.repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class TimeTableUpdateService {

    private final TimeTableRepository timeTableRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void update(TimeBlockReplaceEvent event) {
        TimeTable timeTable = getTimeTableByRoomUuid(event);
        timeTable.updateCount(event.getOldAvailableDateTimes(), event.getNewAvailableDateTimes());
    }

    private TimeTable getTimeTableByRoomUuid(final TimeBlockReplaceEvent event) {
        return timeTableRepository.findByRoomUuid(event.getRoomUuid())
                .orElseThrow(() -> new NotFoundException("해당하는 TimeBlock을 찾을 수 없습니다."));
    }
}
