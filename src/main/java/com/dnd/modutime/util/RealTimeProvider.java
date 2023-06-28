package com.dnd.modutime.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

@Component
public class RealTimeProvider implements TimeProvider {
    @Override
    public LocalDateTime getCurrentLocalDateTime() {
        ZoneId seoulZoneId = ZoneId.of("Asia/Seoul");
        return ZonedDateTime.now(seoulZoneId).toLocalDateTime();
    }
}
