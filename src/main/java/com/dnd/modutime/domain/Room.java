package com.dnd.modutime.domain;

import com.dnd.modutime.util.TimeProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class Room {
    private static final int ZERO_HEAD_COUNT = 0;
    private static final LocalTime ZERO_TIME = LocalTime.of(0, 0);

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
        if (isZeroTime(startTime, endTime)) {
            return;
        }

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("시작시간은 끝나는 시간보다 작아야 합니다.");
        }
    }

    private boolean isZeroTime(LocalTime startTime, LocalTime endTime) {
        return startTime.equals(ZERO_TIME) && endTime.equals(ZERO_TIME);
    }

    private void validateDates(List<LocalDate> dates) {
        if (dates == null) {
            throw new IllegalArgumentException("날짜는 null일 수 없습니다.");
        }
        if (dates.isEmpty()) {
            throw new IllegalArgumentException("날짜는 최소 1개이상 존재해야 합니다.");
        }
    }

    private void validateHeadCount(int headCount) {
        if (headCount < 0) {
            throw new IllegalArgumentException("방 참여 인원은 음수일 수 없습니다.");
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

    public boolean hasStartAndEndTime() {
        return !isZeroTime(startTime, endTime);
    }

    public boolean hasParticipants() {
        return headCount != ZERO_HEAD_COUNT;
    }

    public boolean hasDeadLine() {
        return deadLine != null;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTitle() {
        return title;
    }
}
