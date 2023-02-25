package com.dnd.modutime.application;

import com.dnd.modutime.domain.room.RoomCreationEvent;
import com.dnd.modutime.domain.timetable.TimeTable;
import com.dnd.modutime.repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@RequiredArgsConstructor
public class TimeTableCreateService {

    private final TimeTableRepository timeTableRepository;
    private final TimeTableInitializer timeTableInitializer;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void create(RoomCreationEvent event) {
        TimeTable timeTable = timeTableRepository.save(new TimeTable(event.getUuid()));
        timeTableInitializer.initialize(event.getUuid(), timeTable);
    }
}
