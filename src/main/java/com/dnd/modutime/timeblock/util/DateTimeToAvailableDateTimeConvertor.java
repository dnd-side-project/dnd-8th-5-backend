package com.dnd.modutime.timeblock.util;

import com.dnd.modutime.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.timeblock.domain.TimeBlock;
import java.time.LocalDateTime;
import java.util.List;

public interface DateTimeToAvailableDateTimeConvertor {

    List<AvailableDateTime> convert(TimeBlock timeBlock,
                                    List<LocalDateTime> dateTimes);
}
