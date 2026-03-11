package com.dnd.modutime.core.notification.application.response;

public record UnreadCountResponse(long count) {
    public static UnreadCountResponse of(long count) {
        return new UnreadCountResponse(count);
    }
}
