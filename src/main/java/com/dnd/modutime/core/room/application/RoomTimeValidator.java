package com.dnd.modutime.core.room.application;

import com.dnd.modutime.core.room.repository.RoomRepository;
import com.dnd.modutime.core.timeblock.application.TimeReplaceValidator;
import com.dnd.modutime.core.room.domain.Room;
import com.dnd.modutime.core.room.domain.RoomDate;
import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.AvailableTime;
import com.dnd.modutime.exception.NotFoundException;
import com.dnd.modutime.util.TimeProvider;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoomTimeValidator implements TimeReplaceValidator {

    private final RoomRepository roomRepository;
    private final TimeProvider timeProvider;

    @Override
    public void validate(String roomUuid, List<AvailableDateTime> availableDateTimes) {
        Room room = getRoomByRoomUuid(roomUuid);
        validateDeadLine(room.getDeadLineOrNull());
        validateContainsAllDates(room, availableDateTimes);
        validateStartAndEndTime(room, availableDateTimes);
    }

    private void validateDeadLine(LocalDateTime deadLineOrNull) {
        LocalDateTime now = timeProvider.getCurrentLocalDateTime();
        if (deadLineOrNull != null && now.isAfter(deadLineOrNull)) {
            throw new IllegalArgumentException("데드라인 이후에는 방을 수정할 수 없습니다.");
        }
    }

    private void validateContainsAllDates(Room room, List<AvailableDateTime> availableDateTimes) {
        List<RoomDate> roomDates = availableDateTimes.stream()
                .map(availableDateTime -> new RoomDate(availableDateTime.getDate()))
                .collect(Collectors.toList());
        if (!room.containsAllDates(roomDates)) {
            throw new IllegalArgumentException();
        }
    }

    private void validateStartAndEndTime(Room room, List<AvailableDateTime> availableDateTimes) {
        if (availableDateTimes.isEmpty()) {
            return;
        }
        if (room.hasStartAndEndTime() && !hasTime(availableDateTimes)) {
            throw new IllegalArgumentException("해당 방에는 시간 값이 필요합니다.");
        }
        if (!room.hasStartAndEndTime() && hasTime(availableDateTimes)) {
            throw new IllegalArgumentException("해당 방에는 날짜만 등록할 수 있습니다.");
        }
        if (!room.hasStartAndEndTime() && !hasTime(availableDateTimes)) {
            return;
        }
        availableDateTimes.forEach(it -> validateIncludeTimes(room, it.getTimesOrNull()));
    }

    private void validateIncludeTimes(Room room, List<AvailableTime> availableTimes) {
        availableTimes.forEach(it -> validateIncludeTime(room, it.getTime()));
    }

    private void validateIncludeTime(Room room, LocalTime time) {
        if (!room.includeTime(time)) {
            throw new IllegalArgumentException("방의 범위 밖의 시간입니다.");
        }
    }

    private boolean hasTime(List<AvailableDateTime> availableDateTimes) {
        return availableDateTimes.stream()
                .allMatch(AvailableDateTime::hasTime);
    }

    private Room getRoomByRoomUuid(String roomUuid) {
        return roomRepository.findByUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 방이 없습니다."));
    }
}
