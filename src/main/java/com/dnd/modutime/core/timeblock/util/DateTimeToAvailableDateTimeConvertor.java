package com.dnd.modutime.core.timeblock.util;

import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.TimeBlock;
import java.time.LocalDateTime;
import java.util.List;

public interface DateTimeToAvailableDateTimeConvertor {

    List<AvailableDateTime> convert(TimeBlock timeBlock,
                                    List<LocalDateTime> dateTimes);
}
