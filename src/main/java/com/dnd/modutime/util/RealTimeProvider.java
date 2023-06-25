package com.dnd.modutime.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

@Component
public class RealTimeProvider implements TimeProvider {
    @Override
    public LocalDateTime getCurrentLocalDateTime() {
        ZoneOffset koreaZoneOffset = ZoneOffset.of("+09:00");
        return ZonedDateTime.now(koreaZoneOffset).toLocalDateTime();
    }
}
