package com.dnd.modutime.core.timeblock.application;

import com.dnd.modutime.core.participant.domain.ParticipantRemovedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TimeBlockEventHandler {

    private final TimeBlockService timeBlockService;

    public TimeBlockEventHandler(TimeBlockService timeBlockService) {
        this.timeBlockService = timeBlockService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handle(ParticipantCreationEvent event) {
        timeBlockService.create(event.getRoomUuid(), event.getName());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handle(ParticipantRemovedEvent event) {
        timeBlockService.remove(event.getRoomUuid(), event.getName());
    }
}
