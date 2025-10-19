package com.dnd.modutime.core.adjustresult.controller.dto;

import com.dnd.modutime.core.adjustresult.application.condition.AdjustmentResultSearchCondition;
import java.util.List;

public record AdjustmentResultRequest(
        String sorted, // TODO: enum 으로 변경
        List<String> participantNames
) {
    public AdjustmentResultSearchCondition toSearchCondition(final String roomUuid) {
        return AdjustmentResultSearchCondition.of(
                roomUuid,
                participantNames == null ? List.of() : participantNames,
                sorted == null ? "fast" : sorted
        );
    }
}
