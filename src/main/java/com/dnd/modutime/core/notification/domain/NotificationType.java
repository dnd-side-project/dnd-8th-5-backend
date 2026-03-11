package com.dnd.modutime.core.notification.domain;

public enum NotificationType {
    AVAILABILITY_REGISTERED("가용시간 등록");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
