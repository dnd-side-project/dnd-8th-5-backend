package com.dnd.modutime.core.adjustresult.application.command;

import com.dnd.modutime.core.timetable.domain.DateInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdjustmentResultReplaceCommand {
    private String roomUuid;
    private List<DateInfo> dateInfos;

    public static AdjustmentResultReplaceCommand of(
            String roomUuid,
            List<DateInfo> dateInfos
    ) {
        var command = new AdjustmentResultReplaceCommand();
        command.roomUuid = roomUuid;
        command.dateInfos = dateInfos;
        return command;
    }
}
