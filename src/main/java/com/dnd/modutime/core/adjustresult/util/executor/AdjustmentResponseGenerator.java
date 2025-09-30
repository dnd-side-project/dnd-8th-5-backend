package com.dnd.modutime.core.adjustresult.util.executor;

import com.dnd.modutime.core.adjustresult.application.CandidateDateTimeSortStandard;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import com.dnd.modutime.core.adjustresult.domain.AdjustmentResult;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.repository.AdjustmentResultRepository;
import com.dnd.modutime.core.adjustresult.util.sorter.CandidateDateTimesSorter;
import com.dnd.modutime.core.adjustresult.util.sorter.CandidateDateTimesSorterFactory;
import com.dnd.modutime.core.participant.application.ParticipantQueryService;
import com.dnd.modutime.core.participant.domain.Participants;
import com.dnd.modutime.exception.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdjustmentResponseGenerator implements AdjustmentResultResponseGenerator {

    private static final int EXPOSURE_SIZE = 5;

    private final AdjustmentResultRepository adjustmentResultRepository;
    private final CandidateDateTimesSorterFactory candidateDateTimesSorterFactory;
    private final ParticipantQueryService participantQueryService;

    @Override
    public AdjustmentResultResponse generate(String roomUuid,
                                             CandidateDateTimeSortStandard candidateDateTimeSortStandard,
                                             List<String> names) {
        AdjustmentResult adjustmentResult = getAdjustmentResultByRoomUuid(roomUuid);
        List<CandidateDateTime> candidateDateTimes = adjustmentResult.getCandidateDateTimes();
        CandidateDateTimesSorter candidateDateTimesSorter = candidateDateTimesSorterFactory.getInstance(candidateDateTimeSortStandard);
        candidateDateTimesSorter.sort(candidateDateTimes);
        var participants = participantQueryService.getByRoomUuid(roomUuid);
        return AdjustmentResultResponse.from(candidateDateTimes.stream()
                        .limit(EXPOSURE_SIZE)
                        .collect(Collectors.toList()),
                new Participants(participants)
        );
    }

    @Override
    public AdjustmentResultResponse v1generate(String roomUuid,
                                               CandidateDateTimeSortStandard candidateDateTimeSortStandard,
                                               List<String> names) {
        var adjustmentResult = getAdjustmentResultByRoomUuid(roomUuid);
        var candidateDateTimes = adjustmentResult.getCandidateDateTimes();
        var candidateDateTimesSorter = candidateDateTimesSorterFactory.getInstance(candidateDateTimeSortStandard);
        candidateDateTimesSorter.sort(candidateDateTimes);
        var participants = participantQueryService.getByRoomUuid(roomUuid);
        return AdjustmentResultResponse.from(new ArrayList<>(candidateDateTimes), new Participants(participants)
        );
    }

    private AdjustmentResult getAdjustmentResultByRoomUuid(String roomUuid) {
        return adjustmentResultRepository.findByRoomUuid(roomUuid)
                .orElseThrow(() -> new NotFoundException("해당하는 방이 없습니다."));
    }
}
