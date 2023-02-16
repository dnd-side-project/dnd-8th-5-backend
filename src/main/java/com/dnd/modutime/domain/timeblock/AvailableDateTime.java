package com.dnd.modutime.domain.timeblock;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AvailableDateTime {

    private LocalDate date;
    private List<LocalTime> times;

    public AvailableDateTime(LocalDate date, List<LocalTime> times) {
        validateDate(date);

        this.date = date;
        this.times = times;
    }

    private void validateDate(final LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("date는 null일 수 없습니다.");
        }
    }

    public LocalDate getDate() {
        return date;
    }

    public List<LocalTime> getTimesOrNull() {
        return times;
    }
}
