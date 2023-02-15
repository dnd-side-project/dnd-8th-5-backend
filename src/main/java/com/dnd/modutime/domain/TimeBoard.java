package com.dnd.modutime.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TimeBoard {

    private Long id;
    private String roomUuid;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<LocalDate> dates;

    public TimeBoard(String roomUuid,
                     LocalTime startTime,
                     LocalTime endTime,
                     List<LocalDate> dates) {
        validateRoomUuid(roomUuid);
        validateDates(dates);
        this.roomUuid = roomUuid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dates = dates;
    }

    private void validateRoomUuid(final String roomUuid) {
        if (roomUuid == null) {
            throw new IllegalArgumentException("TimeBoard 생성시 roomUuid가 존재해야 합니다.");
        }
    }

    private void validateDates(final List<LocalDate> dates) {
        if (dates == null || dates.isEmpty()) {
            throw new IllegalArgumentException("TimeBoard 생성시 날짜가 존재해야 합니다");
        }
    }

    public String getRoomUuid() {
        return roomUuid;
    }
}
