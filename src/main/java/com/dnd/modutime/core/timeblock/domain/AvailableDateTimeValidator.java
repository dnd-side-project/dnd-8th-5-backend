package com.dnd.modutime.core.timeblock.domain;

import java.util.List;

public interface AvailableDateTimeValidator {

    void validate(String roomUuid, List<AvailableDateTime> availableDateTimes);
}
