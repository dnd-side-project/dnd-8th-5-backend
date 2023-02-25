package com.dnd.modutime.application;

import com.dnd.modutime.domain.timeblock.AvailableDateTime;
import com.dnd.modutime.domain.timeblock.AvailableTime;
import com.dnd.modutime.domain.timeblock.DateTime;
import com.dnd.modutime.domain.timeblock.TimeBlock;
import com.dnd.modutime.repository.TimeBlockRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationTimeTableCalculator implements TimeTableCalculator{

    private final TimeBlockRepository timeBlockRepository;

    @Override
    public Map<DateTime, Integer> calculate(String roomUuid) {
        List<TimeBlock> timeBlocks = timeBlockRepository.findByRoomUuid(roomUuid);
        Map<DateTime, Integer> countsByDateTime = new HashMap<>();

        for (TimeBlock timeBlock : timeBlocks) {
            List<AvailableDateTime> availableDateTimes = timeBlock.getAvailableDateTimes();
            mergeByAvailableDateTime(countsByDateTime, availableDateTimes);
        }
        return countsByDateTime;
    }

    private void mergeByAvailableDateTime(Map<DateTime, Integer> countsByDateTime,
                                          List<AvailableDateTime> availableDateTimes) {
        for (AvailableDateTime availableDateTime : availableDateTimes) {
            mergeByAvailableTime(countsByDateTime, availableDateTime.getDate(), availableDateTime.getTimesOrNull());
        }
    }

    private void mergeByAvailableTime(Map<DateTime, Integer> countsByDateTime,
                                      LocalDate date,
                                      List<AvailableTime> timesOrNull) {
        if (timesOrNull == null) {
            countsByDateTime.merge(DateTime.of(date, null), 1, Integer::sum);
            return;
        }

        for (AvailableTime availableTime : timesOrNull) {
            LocalTime time = availableTime.getTime();
            countsByDateTime.merge(DateTime.of(date, time), 1, Integer::sum);
        }
    }
}
