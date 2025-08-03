package com.dnd.modutime.core.timeblock.domain;

import com.dnd.modutime.core.timetable.application.command.TimeTableUpdateCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TimeBlockReplaceEvent {

    private String roomUuid;
    private List<AvailableDateTime> oldAvailableDateTimes;
    private List<AvailableDateTime> newAvailableDateTimes;
    private String participantName;

    public TimeTableUpdateCommand toTimeTableUpdateCommand() {
        return TimeTableUpdateCommand.of(
                this.roomUuid,
                this.oldAvailableDateTimes,
                this.newAvailableDateTimes,
                this.participantName
        );
    }
}
