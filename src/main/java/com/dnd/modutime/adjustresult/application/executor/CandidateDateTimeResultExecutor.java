package com.dnd.modutime.adjustresult.application.executor;

import com.dnd.modutime.adjustresult.application.CandidateDateTimeDto;
import com.dnd.modutime.adjustresult.application.SortedBy;
import java.util.List;

public interface CandidateDateTimeResultExecutor {

    List<CandidateDateTimeDto> execute(String roomUuid, SortedBy sortedBy, List<String> names);
}
