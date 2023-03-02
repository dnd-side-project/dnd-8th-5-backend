package com.dnd.modutime.adjustresult.application.executor;

import com.dnd.modutime.adjustresult.application.CandidateDateTimeSorter;
import com.dnd.modutime.adjustresult.application.SortedBy;
import com.dnd.modutime.adjustresult.domain.AdjustmentResult;
import com.dnd.modutime.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.adjustresult.domain.CandidateDateTimeParticipantName;
import com.dnd.modutime.adjustresult.repository.AdjustmentResultRepository;
import com.dnd.modutime.dto.response.AdjustmentResultResponse;
import com.dnd.modutime.dto.response.CandidateDateTimeResponse;
import com.dnd.modutime.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdjustmentResponseGenerator implements AdjustmentResultResponseGenerator {

    private final AdjustmentResultRepository adjustmentResultRepository;
    private final CandidateDateTimeSorter candidateDateTimeSorter;

    @Override
    public AdjustmentResultResponse generate(String roomUuid,
                                             SortedBy sortedBy,
                                             List<String> names) {
        AdjustmentResult adjustmentResult = getAdjustmentResultByRoomUuid(roomUuid);
        List<CandidateDateTime> candidateDateTimes = adjustmentResult.getCandidateDateTimes();
        // candidateDateTimes 정렬
        candidateDateTimeSorter.sort()

        return AdjustmentResultResponse.from(candidateDateTimes);
    }

    private AdjustmentResult getAdjustmentResultByRoomUuid(String roomUuid) {
        return adjustmentResultRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 방이 없습니다."));
    }
}
