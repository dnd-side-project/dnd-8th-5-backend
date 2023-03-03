package com.dnd.modutime.timeblock.util;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DateTimeToAvailableDateTimeConvertorFactory {

    private final Map<String, DateTimeToAvailableDateTimeConvertor> convertors;

    public DateTimeToAvailableDateTimeConvertor getInstance(boolean hasTime) {
        if (hasTime) {
            return convertors.get("dateTimeConvertor");
        }
        return convertors.get("dateConvertor");
    }
}
