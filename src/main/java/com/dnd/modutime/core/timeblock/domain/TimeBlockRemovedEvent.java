package com.dnd.modutime.core.timeblock.domain;

import com.dnd.modutime.core.timetable.application.command.TimeTableUpdateCommand;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeBlockRemovedEvent {
    private String roomUuid;
    private List<AvailableDateTime> oldAvailableDateTimes;
    private List<AvailableDateTime> newAvailableDateTimes;
    private String participantName;

    public static TimeBlockRemovedEvent of(
            String roomUuid,
            List<AvailableDateTime> oldAvailableDateTimes,
            List<AvailableDateTime> newAvailableDateTimes,
            String participantName) {
        var event = new TimeBlockRemovedEvent();
        event.roomUuid = roomUuid;
        event.oldAvailableDateTimes = oldAvailableDateTimes;
        event.newAvailableDateTimes = newAvailableDateTimes;
        event.participantName = participantName;
        return event;
    }

    public TimeTableUpdateCommand toTimeTableUpdateCommand() {
        return TimeTableUpdateCommand.of(
                this.roomUuid,
                this.oldAvailableDateTimes,
                this.newAvailableDateTimes,
                this.participantName
        );
    }
}
