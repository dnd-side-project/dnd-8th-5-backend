package com.dnd.modutime.util;

import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class RealTimeProvider implements TimeProvider {
    @Override
    public LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }
}
