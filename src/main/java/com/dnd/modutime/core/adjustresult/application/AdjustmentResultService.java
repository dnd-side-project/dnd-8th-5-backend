package com.dnd.modutime.core.adjustresult.application;

import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import com.dnd.modutime.core.adjustresult.domain.AdjustmentResult;
import com.dnd.modutime.core.adjustresult.repository.AdjustmentResultRepository;
import com.dnd.modutime.core.adjustresult.util.executor.AdjustmentResultExecutorFactory;
import com.dnd.modutime.core.adjustresult.util.executor.AdjustmentResultResponseGenerator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AdjustmentResultService {

    private final AdjustmentResultRepository adjustmentResultRepository;
    private final AdjustmentResultExecutorFactory adjustmentResultExecutorFactory;

    @Transactional(readOnly = true)
    public AdjustmentResultResponse getByRoomUuidAndSortedAndNames(String roomUuid,
                                                                   String sorted,
                                                                   List<String> names) {
        AdjustmentResultResponseGenerator adjustmentResultResponseGenerator = adjustmentResultExecutorFactory.getInstance(roomUuid, names);
        return adjustmentResultResponseGenerator.generate(roomUuid, CandidateDateTimeSortStandard.getByValue(sorted), names);
    }

    @Transactional(readOnly = true)
    public AdjustmentResultResponse v1getByRoomUuidAndSortedAndNames(String roomUuid,
                                                                     String sorted,
                                                                     List<String> names) {
        var adjustmentResultResponseGenerator = adjustmentResultExecutorFactory.getInstance(roomUuid, names);
        return adjustmentResultResponseGenerator.v1generate(roomUuid, CandidateDateTimeSortStandard.getByValue(sorted), names);
    }

    public void create(String roomUuid) {
        adjustmentResultRepository.save(new AdjustmentResult(roomUuid, List.of()));
    }
}
