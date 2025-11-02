package com.dnd.modutime.core.adjustresult.util.executor;

import com.dnd.modutime.core.participant.application.ParticipantQueryService;
import com.dnd.modutime.core.participant.domain.Participants;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AdjustmentResultExecutorFactory {

    private final Map<String, AdjustmentResultResponseGenerator> executors;
    private final ParticipantQueryService participantQueryService;

    public AdjustmentResultResponseGenerator getInstance(String roomUuid, List<String> names) {
        var participants = new Participants(participantQueryService.getByRoomUuid(roomUuid));
        if (!participants.containsAll(names)) {
            throw new IllegalArgumentException("방에 존재하지 않는 이름이 있습니다.");
        }
        if (Objects.isNull(names) || names.isEmpty()) {
            return executors.get("adjustmentResponseGenerator");
        }
        if (participants.isSameAllNames(names)) {
            return executors.get("adjustmentResponseGenerator");
        }
        return executors.get("timeTableResponseGenerator");
    }
}
