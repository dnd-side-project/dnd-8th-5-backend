package com.dnd.modutime.domain.room;

import com.dnd.modutime.util.TimeProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class Room {

    private String title;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final List<LocalDate> dates;
    private final Integer headCount;
    private final String uuid;
    private final LocalDateTime deadLine;

    public Room(String title,
                LocalTime startTime,
                LocalTime endTime,
                List<LocalDate> dates,
                Integer headCount,
                LocalDateTime deadLine,
                TimeProvider timeProvider) {

        validateTitle(title);
        validateStartAndEndTime(startTime, endTime);
        validateDates(dates);
        validateHeadCount(headCount);
        validateDeadLine(deadLine, timeProvider);

        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dates = dates;
        this.headCount = headCount;
        this.uuid = UUID.randomUUID().toString();
        this.deadLine = deadLine;
    }

    private void validateTitle(final String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("방의 제목은 빈문자일 수 없습니다.");
        }
    }

    private void validateStartAndEndTime(LocalTime startTime, LocalTime endTime) {
        if (startTime == null && endTime == null) {
            return;
        }

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("시작시간과 끝나는 시간은 하나만 null일 수 없습니다.");
        }


        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("시작시간은 끝나는 시간보다 작아야 합니다.");
        }
    }

    private void validateHeadCount(Integer headCount) {
        if (headCount == null) {
            return;
        }

        if (headCount < 0) {
            throw new IllegalArgumentException("방 참여 인원은 음수일 수 없습니다.");
        }
    }

    private void validateDates(List<LocalDate> dates) {
        if (dates == null) {
            throw new IllegalArgumentException("날짜는 null일 수 없습니다.");
        }
        if (dates.isEmpty()) {
            throw new IllegalArgumentException("날짜는 최소 1개이상 존재해야 합니다.");
        }
    }

    private void validateDeadLine(LocalDateTime deadLine,
                                  TimeProvider timeProvider) {
        if (deadLine == null) {
            return;
        }

        LocalDateTime now = timeProvider.getCurrentLocalDateTime();
        if (!now.isBefore(deadLine)) {
            throw new IllegalArgumentException("마감시간은 현재시간 이후여야 합니다.");
        }
    }

    public String getTitle() {
        return title;
    }

    public LocalTime getStartTimeOrNull() {
        return startTime;
    }

    public LocalTime getEndTimeOrNull() {
        return endTime;
    }

    public List<LocalDate> getDates() {
        return dates;
    }

    public Integer getHeadCountOrNull() {
        return headCount;
    }

    public String getUuid() {
        return uuid;
    }

    public LocalDateTime getDeadLineOrNull() {
        return deadLine;
    }
}
