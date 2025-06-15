package com.dnd.modutime.core.adjustresult.application;

import com.dnd.modutime.core.timetable.domain.TimeTableReplaceEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AdjustmentResultEventHandler {

    private final AdjustmentResultReplaceService adjustmentResultReplaceService;

    public AdjustmentResultEventHandler(AdjustmentResultReplaceService adjustmentResultReplaceService) {
        this.adjustmentResultReplaceService = adjustmentResultReplaceService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener
    public void handle(TimeTableReplaceEvent event) {
        var command = event.toAdjustmentResultReplaceCommand();
        adjustmentResultReplaceService.replace(command);
    }
}
