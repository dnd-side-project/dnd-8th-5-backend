package com.dnd.modutime.dto.response;

import com.dnd.modutime.timetable.domain.DateInfo;
import com.dnd.modutime.timetable.domain.TimeTable;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TimeTableResponse {

    @JsonProperty(value = "availableDateTimes")
    private List<TimeAndCountPerDate> timeAndCountPerDates;

    public static TimeTableResponse from(TimeTable timeTable) {
        List<TimeAndCountPerDate> timeAndCountPerDates = new ArrayList<>();
        List<DateInfo> dateInfos = timeTable.getDateInfos();
        for (DateInfo dateInfo : dateInfos) {
            List<AvailableTimeInfo> availableTimeInfos = dateInfo.getTimeInfos().stream()
                    .map(timeInfo -> new AvailableTimeInfo(timeInfo.getTime(), timeInfo.getParticipantsSize()))
                    .collect(Collectors.toList());
            TimeAndCountPerDate timeAndCountPerDate = new TimeAndCountPerDate(dateInfo.getDate(), availableTimeInfos);
            timeAndCountPerDates.add(timeAndCountPerDate);
        }
        return new TimeTableResponse(timeAndCountPerDates);
    }
}
