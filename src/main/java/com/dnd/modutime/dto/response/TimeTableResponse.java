package com.dnd.modutime.dto.response;

import com.dnd.modutime.domain.timeblock.DateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeTableResponse {

    @JsonProperty(value = "availableDateTimes")
    private List<TimeAndCountPerDate> timeAndCountPerDates;

    public static TimeTableResponse from(Map<DateTime, Integer> countsByDateTime) {

//        for (Entry<DateTime, Integer> dateTimeIntegerEntry : countsByDateTime.entrySet()) {
//            final DateTime dateTime = dateTimeIntegerEntry.getKey();
//            LocalDate date = dateTime.getDate();
//            LocalTime time = dateTime.getTime();
//            Integer count = dateTimeIntegerEntry.getValue();
//
//            Map<LocalDate, List<AvailableTimeInfo>> dateTimes = new HashMap<>();
//
//            dateTimes.merge(date, new AvailableTimeInfo(time, count), );
//        }
//
//        countsByDateTime.keySet().stream()
//                .map(dateTime -> new TimeAndCountPerDate(dateTime.getDate(), ))
        return new TimeTableResponse();
    }
}
