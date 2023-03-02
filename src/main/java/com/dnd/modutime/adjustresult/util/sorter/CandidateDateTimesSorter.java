package com.dnd.modutime.adjustresult.util.sorter;

import com.dnd.modutime.adjustresult.domain.CandidateDateTime;
import java.util.List;

public interface CandidateDateTimesSorter {
    void sort(List<CandidateDateTime> candidateDateTimes);
}
