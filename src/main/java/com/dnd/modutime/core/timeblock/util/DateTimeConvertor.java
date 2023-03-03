package com.dnd.modutime.core.timeblock.util;

import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.AvailableTime;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

// TODO: test
@Component
public class DateTimeConvertor implements DateTimeToAvailableDateTimeConvertor {

    @Override
    public List<AvailableDateTime> convert(TimeBlock timeBlock,
                                           List<LocalDateTime> dateTimes) {
        List<AvailableDateTime> availableDateTimes = new ArrayList<>();
        HashMap<LocalDate, List<LocalTime>> dates = new HashMap<>();
        dateTimes.forEach(dateTime -> addTime(dates, dateTime.toLocalDate(), dateTime.toLocalTime()));
        for (LocalDate localDate : dates.keySet()) {
            availableDateTimes.add(new AvailableDateTime(timeBlock, localDate, dates.get(localDate).stream()
                    .map(AvailableTime::new)
                    .collect(Collectors.toList())));
        }
        return availableDateTimes;
    }

    private void addTime(HashMap<LocalDate, List<LocalTime>> dates, LocalDate date, LocalTime time) {
        if (!dates.containsKey(date)) {
            dates.put(date, new ArrayList<>(List.of(time)));
            return;
        }
        dates.get(date).add(time);
    }
}
