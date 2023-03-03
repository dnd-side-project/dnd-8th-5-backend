package com.dnd.modutime.core.adjustresult.util.executor;

import com.dnd.modutime.core.adjustresult.util.sorter.CandidateDateTimesSorter;
import com.dnd.modutime.core.adjustresult.application.CandidateDateTimeSortStandard;
import com.dnd.modutime.core.adjustresult.util.sorter.CandidateDateTimesSorterFactory;
import com.dnd.modutime.core.adjustresult.domain.AdjustmentResult;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.repository.AdjustmentResultRepository;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import com.dnd.modutime.exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdjustmentResponseGenerator implements AdjustmentResultResponseGenerator {

    private final AdjustmentResultRepository adjustmentResultRepository;
    private final CandidateDateTimesSorterFactory candidateDateTimesSorterFactory;

    @Override
    public AdjustmentResultResponse generate(String roomUuid,
                                             CandidateDateTimeSortStandard candidateDateTimeSortStandard,
                                             List<String> names) {
        AdjustmentResult adjustmentResult = getAdjustmentResultByRoomUuid(roomUuid);
        List<CandidateDateTime> candidateDateTimes = adjustmentResult.getCandidateDateTimes();
        CandidateDateTimesSorter candidateDateTimesSorter = candidateDateTimesSorterFactory.getInstance(candidateDateTimeSortStandard);
        candidateDateTimesSorter.sort(candidateDateTimes);
        return AdjustmentResultResponse.from(candidateDateTimes.stream()
                .limit(5)
                .collect(Collectors.toList())
        );
    }

    private AdjustmentResult getAdjustmentResultByRoomUuid(String roomUuid) {
        return adjustmentResultRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 방이 없습니다."));
    }
}
