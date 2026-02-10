package com.dnd.modutime.core.timeblock.application.request;

import com.dnd.modutime.core.timeblock.application.command.TimeReplaceCommand;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TimeReplaceRequestV1 {

    private Boolean hasTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private List<LocalDateTime> availableDateTimes;

    public TimeReplaceCommand toCommand(String roomId, String participantName) {
        return TimeReplaceCommand.of(roomId, participantName, hasTime, availableDateTimes);
    }
}
