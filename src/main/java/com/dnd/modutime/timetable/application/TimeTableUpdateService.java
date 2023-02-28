package com.dnd.modutime.timetable.application;

import com.dnd.modutime.exception.NotFoundException;
import com.dnd.modutime.timeblock.domain.TimeBlockReplaceEvent;
import com.dnd.modutime.timetable.domain.TimeInfo;
import com.dnd.modutime.timetable.domain.TimeTable;
import com.dnd.modutime.timetable.domain.TimeTableParticipantName;
import com.dnd.modutime.timetable.repository.TimeTableRepository;
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
//    private final TimeTableParticipantNameRepository timeTableParticipantNameRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void update(TimeBlockReplaceEvent event) {
        TimeTable timeTable = getTimeTableByRoomUuid(event.getRoomUuid());


        List<TimeInfo> oldTimeInfos = timeTable.getTimeInfosByAvailableDateTimesAndParticipantName(
                event.getOldAvailableDateTimes(),
                event.getParticipantName()
        );

        for (TimeInfo timeInfo : oldTimeInfos) {
            timeTableParticipantNameRepository.deleteByTimeInfoId(timeInfo.getId());
        }

        List<TimeInfo> newTimeInfos = timeTable.getTimeInfosByAvailableDateTimesAndParticipantName(event.getNewAvailableDateTimes());
        for (TimeInfo timeInfo : newTimeInfos) {
            timeTableParticipantNameRepository.save(new TimeTableParticipantName(timeInfo, event.getParticipantName()));
        }

        timeTable.updateParticipantName(event.getOldAvailableDateTimes(),
                event.getNewAvailableDateTimes(),
                event.getParticipantName());
    }

    private TimeTable getTimeTableByRoomUuid(String roomUuid) {
        return timeTableRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 TimeBlock을 찾을 수 없습니다."));
    }
}
