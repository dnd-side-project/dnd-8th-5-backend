package com.dnd.modutime.core.timetable.domain.view;

import com.dnd.modutime.core.timetable.domain.DateInfo;
import com.dnd.modutime.core.timetable.domain.TimeInfo;
import com.dnd.modutime.core.timetable.domain.TimeTable;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeTableOverview {

    @JsonProperty(value = "availableDateTimes")
    private List<TimeAndCountPerDate> timeAndCountPerDates;

    private TimeTableOverview(List<TimeAndCountPerDate> timeAndCountPerDates) {
        this.timeAndCountPerDates = timeAndCountPerDates;
    }

    public static TimeTableOverview from(TimeTable timeTable, List<String> participantNames) {
        var timeAndCountPerDates = timeTable.getDateInfos().stream()
                .map(dateInfo -> createTimeAndCountPerDate(dateInfo, participantNames))
                .collect(Collectors.toList());

        return new TimeTableOverview(timeAndCountPerDates);
    }

    private static TimeAndCountPerDate createTimeAndCountPerDate(DateInfo dateInfo, List<String> participantNames) {
        var timeInfos = dateInfo.getTimeInfos();
        var availableTimeInfos = timeInfos.stream()
                .map(timeInfo -> AvailableTimeInfo.from(timeInfo, participantNames))
                .collect(Collectors.toList());
        return TimeAndCountPerDate.of(dateInfo.getDate(), availableTimeInfos);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class TimeAndCountPerDate {

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate availableDate;
        private List<AvailableTimeInfo> availableTimeInfos;

        public static TimeAndCountPerDate of(LocalDate date, List<AvailableTimeInfo> availableTimeInfos) {
            var timeAndCountPerDate = new TimeAndCountPerDate();
            timeAndCountPerDate.availableDate = date;
            timeAndCountPerDate.availableTimeInfos = availableTimeInfos;
            return timeAndCountPerDate;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class AvailableTimeInfo {

        private long timeInfoId;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
        private LocalTime time;
        private int count;

        public static AvailableTimeInfo from(TimeInfo timeInfo, List<String> participantNames) {
            var info = new AvailableTimeInfo();
            info.timeInfoId = timeInfo.getId();
            info.time = timeInfo.getTime();
            info.count = timeInfo.getParticipantsSize(participantNames);

            return info;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final AvailableTimeInfo that = (AvailableTimeInfo) o;

            if (getCount() != that.getCount()) {
                return false;
            }
            return getTime() != null ? getTime().equals(that.getTime()) : that.getTime() == null;
        }

        @Override
        public int hashCode() {
            int result = getTime() != null ? getTime().hashCode() : 0;
            result = 31 * result + getCount();
            return result;
        }
    }
}