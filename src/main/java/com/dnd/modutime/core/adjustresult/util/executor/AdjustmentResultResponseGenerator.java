package com.dnd.modutime.core.adjustresult.util.executor;

import com.dnd.modutime.core.Page;
import com.dnd.modutime.core.Pageable;
import com.dnd.modutime.core.adjustresult.application.CandidateDateTimeSortStandard;
import com.dnd.modutime.core.adjustresult.application.condition.AdjustmentResultSearchCondition;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import com.dnd.modutime.core.adjustresult.application.response.CandidateDateTimeResponseV1;
import java.util.List;

public interface AdjustmentResultResponseGenerator {

    AdjustmentResultResponse generate(String roomUuid,
                                      CandidateDateTimeSortStandard candidateDateTimeSortStandard,
                                      List<String> names);

    Page<CandidateDateTimeResponseV1> v1generate(final AdjustmentResultSearchCondition condition,
                                                 final Pageable pageable);
}
