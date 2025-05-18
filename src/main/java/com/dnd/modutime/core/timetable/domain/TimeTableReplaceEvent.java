package com.dnd.modutime.core.timetable.domain;

import com.dnd.modutime.core.adjustresult.application.command.AdjustmentResultReplaceCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TimeTableReplaceEvent {

    private final String roomUuid;
    private final List<DateInfo> dateInfos;

    public AdjustmentResultReplaceCommand toAdjustmentResultReplaceCommand() {
        return AdjustmentResultReplaceCommand.of(
                this.roomUuid,
                this.dateInfos
        );
    }
}
