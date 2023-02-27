package com.dnd.modutime.timetable.application;

import com.dnd.modutime.timetable.domain.TimeTable;
import com.dnd.modutime.timetable.repository.TimeTableRepository;
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
