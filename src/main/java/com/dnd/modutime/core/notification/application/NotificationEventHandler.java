package com.dnd.modutime.core.notification.application;

import com.dnd.modutime.core.timeblock.domain.TimeBlockReplaceEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class NotificationEventHandler {

    private final NotificationService notificationService;

    public NotificationEventHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TimeBlockReplaceEvent event) {
        try {
            notificationService.sendReplaceMessage(
                    event.getRoomUuid(),
                    event.getParticipantName()
            );
        } catch (Exception e) {
            log.warn("알림 발송 실패 - room: {}, participant: {}, error: {}",
                    event.getRoomUuid(), event.getParticipantName(), e.getMessage());
        }
    }
}
