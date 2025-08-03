package com.dnd.modutime.core.timetable.application;

import com.dnd.modutime.core.timetable.domain.view.TimeTableOverview;
import com.dnd.modutime.core.timetable.domain.view.TimeTableSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeTableFacade {

    private final TimeTableQueryService timeTableQueryService;

    /**
     * roomUuid와 participantName을 기반으로 TimeTableOverview를 조회합니다.
     * 
     * @param condition
     * @return
     */
    public TimeTableOverview getOverview(TimeTableSearchCondition condition) {
        var timeTable = this.timeTableQueryService.findByRoomUuid(condition.getRoomUuid());

        return TimeTableOverview.from(timeTable, condition.getParticipantName());
    }
}
