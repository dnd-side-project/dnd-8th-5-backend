package com.dnd.modutime.core.adjustresult.util.sorter;

import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import java.util.Comparator;
import java.util.List;

public interface CandidateDateTimesSorter {
    void sort(List<CandidateDateTime> candidateDateTimes);

    Comparator<CandidateDateTime> getComparator();
}
