package com.dnd.modutime.core.notification.domain;

import java.util.List;
import java.util.Map;

public interface NotificationSender {
    NotificationSendResult send(List<String> tokens, String title, String body, Map<String, String> data);
}
