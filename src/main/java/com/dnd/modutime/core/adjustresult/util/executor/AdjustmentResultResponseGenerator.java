package com.dnd.modutime.core.adjustresult.util.executor;

import com.dnd.modutime.core.adjustresult.application.CandidateDateTimeSortStandard;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponse;
import com.dnd.modutime.core.adjustresult.application.response.AdjustmentResultResponseV1;
import java.util.List;

public interface AdjustmentResultResponseGenerator {

    AdjustmentResultResponse generate(String roomUuid,
                                      CandidateDateTimeSortStandard candidateDateTimeSortStandard,
                                      List<String> names);

    AdjustmentResultResponseV1 v1generate(String roomUuid,
                                          CandidateDateTimeSortStandard candidateDateTimeSortStandard,
                                          List<String> names);
}
