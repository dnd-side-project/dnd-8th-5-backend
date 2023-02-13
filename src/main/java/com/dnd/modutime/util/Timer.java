package com.dnd.modutime.util;

import java.time.LocalDateTime;

public class Timer {

    private static final int MIN = 0;

    public static LocalDateTime calculateDeadLine(int day,
                                                  int hour,
                                                  int minute,
                                                  TimeProvider timeProvider) {
        validateNegative(day, hour, minute);
        LocalDateTime now = timeProvider.getCurrentLocalDateTime();
        return now.plusMinutes(minute)
                .plusHours(hour)
                .plusDays(day);
    }

    private static void validateNegative(int day, int hour, int minute) {
        if (day < MIN || hour < MIN || minute < MIN) {
            throw new IllegalArgumentException("시간값에는 음수가 들어올 수 없습니다.");
        }
    }
}
