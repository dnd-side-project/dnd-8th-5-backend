package com.dnd.modutime.infrastructure.fcm;

import com.dnd.modutime.core.notification.domain.NotificationSendResult;
import com.dnd.modutime.core.notification.domain.NotificationSender;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class NoOpNotificationSender implements NotificationSender {

    @Override
    public NotificationSendResult send(List<String> tokens, String title, String body, Map<String, String> data) {
        log.debug("NoOp 알림 발송 - tokens: {}", tokens.size());
        return NotificationSendResult.success(tokens.size());
    }
}
