package com.dnd.modutime.core.user;

import java.time.LocalDateTime;

public record UserCreatedEvent(

        Long userId,
        LocalDateTime createdTime
) {
}
