package com.dnd.modutime.util;

import java.time.LocalDateTime;

public class FakeTimeProvider implements TimeProvider {

    private LocalDateTime dateTime = LocalDateTime.of(2023, 2, 9, 0, 0);

    @Override
    public LocalDateTime getCurrentLocalDateTime() {
        return dateTime;
    }

    public void setTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
