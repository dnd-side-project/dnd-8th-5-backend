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

    // TODO: 알림 기능 임시 비활성화
//    @Async
//    @TransactionalEventListener
//    public void handle(TimeBlockReplaceEvent event) {
//        try {
//            notificationService.sendReplaceMessage(
//                    event.getRoomUuid(),
//                    event.getParticipantName()
//            );
//        } catch (Exception e) {
//            log.warn("알림 발송 실패 - room: {}, participant: {}, error: {}",
//                    event.getRoomUuid(), event.getParticipantName(), e.getMessage());
//        }
//    }
}
