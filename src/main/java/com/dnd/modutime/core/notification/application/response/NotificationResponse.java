package com.dnd.modutime.core.notification.application.response;

import com.dnd.modutime.core.notification.domain.Notification;
import com.dnd.modutime.core.notification.domain.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String title,
        String message,
        String roomUuid,
        String senderName,
        @JsonProperty("isRead") boolean read,
        LocalDateTime createdAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getRoomUuid(),
                notification.getSenderName(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }
}
