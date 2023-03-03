package com.dnd.modutime.core.timeblock.application;

import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import java.util.List;

public interface TimeReplaceValidator {

    void validate(String roomUuid, List<AvailableDateTime> availableDateTimes);
}
