package com.dnd.modutime.adjustresult.application.executor;

import com.dnd.modutime.adjustresult.application.SortedBy;
import com.dnd.modutime.dto.response.AdjustmentResultResponse;
import java.util.List;

public interface AdjustmentResultResponseGenerator {

    AdjustmentResultResponse generate(String roomUuid,
                                      SortedBy sortedBy,
                                      List<String> names);
}
