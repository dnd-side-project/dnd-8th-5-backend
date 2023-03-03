package com.dnd.modutime.core.adjustresult.util.executor;

import com.dnd.modutime.core.adjustresult.application.CandidateDateTimeSortStandard;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import java.util.List;

public interface AdjustmentResultResponseGenerator {

    AdjustmentResultResponse generate(String roomUuid,
                                      CandidateDateTimeSortStandard candidateDateTimeSortStandard,
                                      List<String> names);
}
