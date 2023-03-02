package com.dnd.modutime.adjustresult.util.executor;

import com.dnd.modutime.adjustresult.application.CandidateDateTimeSortStandard;
import com.dnd.modutime.dto.response.AdjustmentResultResponse;
import java.util.List;

public interface AdjustmentResultResponseGenerator {

    AdjustmentResultResponse generate(String roomUuid,
                                      CandidateDateTimeSortStandard candidateDateTimeSortStandard,
                                      List<String> names);
}
