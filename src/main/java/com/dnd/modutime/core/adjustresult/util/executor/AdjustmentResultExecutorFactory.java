package com.dnd.modutime.core.adjustresult.util.executor;

import com.dnd.modutime.core.participant.domain.Participants;
import com.dnd.modutime.core.participant.repository.ParticipantRepository;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdjustmentResultExecutorFactory {

    private final Map<String, AdjustmentResultResponseGenerator> executors;
    private final ParticipantRepository participantRepository;

    public AdjustmentResultResponseGenerator getInstance(String roomUuid, List<String> names) {
        Participants participants = new Participants(participantRepository.findByRoomUuid(roomUuid));
        if (!participants.containsAll(names)) {
            throw new IllegalArgumentException("방에 존재하지 않는 이름이 있습니다.");
        }
        if (participants.isSameAllNames(names) || names.isEmpty()) {
            return executors.get("adjustmentResponseGenerator");
        }
        return executors.get("timeTableResponseGenerator");
    }
}
