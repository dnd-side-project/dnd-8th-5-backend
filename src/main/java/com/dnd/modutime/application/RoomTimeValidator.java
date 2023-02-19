package com.dnd.modutime.application;

import com.dnd.modutime.domain.timeblock.AvailableDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class RoomTimeValidator implements TimeReplaceValidator{
    @Override
    public void validate(String roomUuid, List<AvailableDateTime> availableDateTimes) {

    }
}
