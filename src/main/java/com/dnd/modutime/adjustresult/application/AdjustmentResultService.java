package com.dnd.modutime.adjustresult.application;

import com.dnd.modutime.adjustresult.util.executor.AdjustmentResultExecutorFactory;
import com.dnd.modutime.adjustresult.util.executor.AdjustmentResultResponseGenerator;
import com.dnd.modutime.adjustresult.domain.AdjustmentResult;
import com.dnd.modutime.adjustresult.repository.AdjustmentResultRepository;
import com.dnd.modutime.dto.response.AdjustmentResultResponse;
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

    public void create(String roomUuid) {
        adjustmentResultRepository.save(new AdjustmentResult(roomUuid, List.of()));
    }
}
