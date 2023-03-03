package com.dnd.modutime.core.timeblock.application.response;

import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import com.dnd.modutime.core.timeblock.domain.AvailableTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TimeBlockResponse {

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private List<LocalDateTime> availableDateTimes;

    public static TimeBlockResponse of(String participantName,
                                       List<AvailableDateTime> availableDateTimes) {
        List<LocalDateTime> dateTimes = new ArrayList<>();
        for (AvailableDateTime availableDateTime : availableDateTimes) {
            addDateTimes(dateTimes, availableDateTime);
        }
        return new TimeBlockResponse(participantName, dateTimes);
    }

    private static void addDateTimes(List<LocalDateTime> dateTimes,
                                     AvailableDateTime availableDateTime) {
        LocalDate date = availableDateTime.getDate();
        List<AvailableTime> timesOrNull = availableDateTime.getTimesOrNull();
        if (timesOrNull.isEmpty()) {
            dateTimes.add(LocalDateTime.of(date, LocalTime.of(0, 0)));
            return;
        }
        for (AvailableTime availableTime : timesOrNull) {
            LocalTime time = availableTime.getTime();
            dateTimes.add(LocalDateTime.of(date, time));
        }
    }
}
