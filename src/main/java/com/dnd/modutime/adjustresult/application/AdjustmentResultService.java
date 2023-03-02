package com.dnd.modutime.adjustresult.application;

import com.dnd.modutime.adjustresult.application.executor.AdjustmentResultResponseGenerator;
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
    private final CandidateDateTimeConvertor candidateDateTimeConvertor;

    @Transactional(readOnly = true)
    public AdjustmentResultResponse getByRoomUuidAndSortedAndNames(String roomUuid,
                                                                   String sorted,
                                                                   List<String> names) {

        // AdjustentResultExecutorFactory : names로 판단후 구현체 반환 (adjustmentResult 이용한 or TimeTable)
        // AdjustentResultExecutor 반환 = AdjustentResultExecutorFactory(roomUuid, names);
        // 1. 전체참여자 -> AdjustmentResult로 부터 받아오기
        // 2. 일부 -> TimeTable로부터 받아오기
        // 구현체에서 알아서 수행후 List<CandidateDateTimeDto> 반환
        // List<CandidateDateTimeDto> -> AdjustmentResultResponse 변환후 반환

        AdjustmentResultResponseGenerator adjustmentResultResponseGenerator = adjustmentResultExecutorFactory.getInstance(roomUuid, names);
        List<CandidateDateTimeDto> candidateDateTimesDto = adjustmentResultResponseGenerator.generate(roomUuid, ,
                names);
        // 정렬

//        List response = candidateDateTimeResultExecutor.execute(roomUuid, names);

        return AdjustmentResultResponse.from(candidateDateTimesDto);
    }

    public void create(String roomUuid) {
        adjustmentResultRepository.save(new AdjustmentResult(roomUuid, List.of()));
    }
}
