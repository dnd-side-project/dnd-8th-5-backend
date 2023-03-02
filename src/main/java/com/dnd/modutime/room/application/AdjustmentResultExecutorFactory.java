package com.dnd.modutime.room.application;

import com.dnd.modutime.adjustresult.application.executor.CandidateDateTimeResultExecutor;
import com.dnd.modutime.dto.response.RoomInfoResponse;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdjustmentResultExecutorFactory {

    private final Map<String, CandidateDateTimeResultExecutor> executors;
    private final RoomService roomService;

    // TODO
    public CandidateDateTimeResultExecutor getInstance(String roomUuid, List<String> names) {
        RoomInfoResponse roomInfoResponse = roomService.getInfo(roomUuid);
        List<String> participantNames = roomInfoResponse.getParticipantNames();
        if (participantNames.containsAll(names) || names.isEmpty()) {
            return executors.get("AdjustmentResultExecutor");
        }
        return executors.get("TimeTableResultExecutor");
    }
}
