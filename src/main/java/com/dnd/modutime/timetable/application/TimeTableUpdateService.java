package com.dnd.modutime.timetable.application;

import com.dnd.modutime.exception.NotFoundException;
import com.dnd.modutime.timeblock.domain.TimeBlockReplaceEvent;
import com.dnd.modutime.timetable.domain.TimeTable;
import com.dnd.modutime.timetable.repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class TimeTableUpdateService {

    private final TimeTableRepository timeTableRepository;
//    private final DateInfoRepository dateInfoRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void update(TimeBlockReplaceEvent event) {
        TimeTable timeTable = getTimeTableByRoomUuid(event.getRoomUuid());
        // 참여자를 지워야함
        // old에 해당하는 timeInfo id 가져와서 delete
        timeTable.getTimeInfoParticipantNameIds();
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
