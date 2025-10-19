package com.dnd.modutime.core.adjustresult.application.condition;

import com.dnd.modutime.core.adjustresult.application.CandidateDateTimeSortStandard;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class AdjustmentResultSearchCondition {
    private String roomUuid;
    private List<String> participantNames;
    private CandidateDateTimeSortStandard sortedStandard;

    public static AdjustmentResultSearchCondition of(String roomUuid, List<String> participantNames, CandidateDateTimeSortStandard sortedStandard) {
        var condition = new AdjustmentResultSearchCondition();
        condition.roomUuid = roomUuid;
        condition.participantNames = participantNames;
        condition.sortedStandard = sortedStandard;
        return condition;
    }
}
