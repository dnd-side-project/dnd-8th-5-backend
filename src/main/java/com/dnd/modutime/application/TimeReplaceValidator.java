package com.dnd.modutime.application;

import com.dnd.modutime.domain.timeblock.AvailableDateTime;
import java.util.List;

public interface TimeReplaceValidator {

    void validate(String roomUuid, List<AvailableDateTime> availableDateTimes);
}
