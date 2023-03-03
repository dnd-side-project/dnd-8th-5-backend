package com.dnd.modutime.core.timeblock.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class DateTime {

    private LocalDate date;
    private LocalTime time;

    private DateTime(LocalDate date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    public static DateTime of(LocalDate date, LocalTime time) {
        return new DateTime(date, time);
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final DateTime dateTime = (DateTime) o;

        if (date != null ? !date.equals(dateTime.date) : dateTime.date != null) {
            return false;
        }
        return time != null ? time.equals(dateTime.time) : dateTime.time == null;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}
