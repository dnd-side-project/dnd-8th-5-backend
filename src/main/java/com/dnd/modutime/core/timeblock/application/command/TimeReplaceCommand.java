package com.dnd.modutime.core.timeblock.application.command;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeReplaceCommand {
    private String roomUuid;
    private String participantName;
    private Boolean hasTime;
    private List<LocalDateTime> availableDateTimes;

    public static TimeReplaceCommand of(String roomUuid,
                                        String participantName,
                                        Boolean hasTime,
                                        List<LocalDateTime> availableDateTimes) {
        var command = new TimeReplaceCommand();
        command.roomUuid = roomUuid;
        command.participantName = participantName;
        command.hasTime = hasTime;
        command.availableDateTimes = availableDateTimes;
        return command;
    }
}
