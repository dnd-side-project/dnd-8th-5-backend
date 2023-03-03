package com.dnd.modutime.core.timeblock.util;

import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

// TODO: test
@Component
public class DateConvertor implements DateTimeToAvailableDateTimeConvertor {

    @Override
    public List<AvailableDateTime> convert(TimeBlock timeBlock,
                                           List<LocalDateTime> dateTimes) {
        return dateTimes.stream()
                .map(it -> new AvailableDateTime(timeBlock, it.toLocalDate(), null))
                .collect(Collectors.toList());
    }
}
