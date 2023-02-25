package com.dnd.modutime.application;

import com.dnd.modutime.domain.timeblock.DateTime;
import java.util.Map;

public interface TimeTableCalculator {

    Map<DateTime, Integer> calculate(String roomUuid);
}
