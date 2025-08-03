package com.dnd.modutime.core.timetable.application.command;

import com.dnd.modutime.core.timeblock.domain.AvailableDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeTableUpdateCommand {
    private String roomUuid;
    private List<AvailableDateTime> oldAvailableDateTimes;
    private List<AvailableDateTime> newAvailableDateTimes;
    private String participantName;

    public static TimeTableUpdateCommand of(
            String roomUuid,
            List<AvailableDateTime> oldAvailableDateTimes,
            List<AvailableDateTime> newAvailableDateTimes,
            String participantName) {
        var command = new TimeTableUpdateCommand();
        command.roomUuid = roomUuid;
        command.oldAvailableDateTimes = oldAvailableDateTimes;
        command.newAvailableDateTimes = newAvailableDateTimes;
        command.participantName = participantName;
        return command;
    }
}
