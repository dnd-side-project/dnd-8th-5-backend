package com.dnd.modutime.core.notification.application.response;

import com.dnd.modutime.core.notification.domain.Notification;
import com.dnd.modutime.core.notification.domain.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Map;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String title,
        String message,
        Map<String, String> data,
        @JsonProperty("isRead") boolean read,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getData(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
