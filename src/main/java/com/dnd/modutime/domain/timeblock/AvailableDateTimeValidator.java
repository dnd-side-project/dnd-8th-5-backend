package com.dnd.modutime.domain.timeblock;

import java.util.List;

public interface AvailableDateTimeValidator {

    void validate(String roomUuid, List<AvailableDateTime> availableDateTimes);
}
