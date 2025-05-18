package com.dnd.modutime.core.timetable.application;

import com.dnd.modutime.core.timeblock.domain.TimeBlockRemovedEvent;
import com.dnd.modutime.core.timeblock.domain.TimeBlockReplaceEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TimeTableEventHandler {

    private final TimeTableService timeTableService;

    public TimeTableEventHandler(TimeTableService timeTableService) {
        this.timeTableService = timeTableService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handle(TimeBlockRemovedEvent event) {
        var command = event.toTimeTableUpdateCommand();
        this.timeTableService.update(command);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handle(TimeBlockReplaceEvent event) {
        var command = event.toTimeTableUpdateCommand();
        this.timeTableService.update(command);
    }
}
