package com.dnd.modutime.core.adjustresult.application;

import com.dnd.modutime.core.Page;
import com.dnd.modutime.core.adjustresult.application.condition.AdjustmentResultSearchCondition;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import com.dnd.modutime.core.adjustresult.application.response.CandidateDateTimeResponseV1;
import com.dnd.modutime.core.adjustresult.domain.AdjustmentResult;
import com.dnd.modutime.core.adjustresult.repository.AdjustmentResultRepository;
import com.dnd.modutime.core.adjustresult.util.executor.AdjustmentResultExecutorFactory;
import com.dnd.modutime.core.adjustresult.util.executor.AdjustmentResultResponseGenerator;
import com.dnd.modutime.infrastructure.PageRequest;
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
    public Page<CandidateDateTimeResponseV1> search(AdjustmentResultSearchCondition condition, PageRequest pageRequest) {
        var adjustmentResultResponseGenerator = this.adjustmentResultExecutorFactory.getInstance(condition.getRoomUuid(), condition.getParticipantNames());
        return adjustmentResultResponseGenerator.v1generate(condition, pageRequest);
    }

    public void create(String roomUuid) {
        adjustmentResultRepository.save(new AdjustmentResult(roomUuid, List.of()));
    }
}
