package com.dnd.modutime.timeblock.domain;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimeBlockReplaceEvent {

    private String roomUuid;
    private List<AvailableDateTime> oldAvailableDateTimes;
    private List<AvailableDateTime> newAvailableDateTimes;
}
