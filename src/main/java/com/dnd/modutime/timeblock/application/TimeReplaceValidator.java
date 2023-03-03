package com.dnd.modutime.timeblock.application;

import com.dnd.modutime.timeblock.domain.AvailableDateTime;
import java.util.List;

public interface TimeReplaceValidator {

    void validate(String roomUuid, List<AvailableDateTime> availableDateTimes);
}
