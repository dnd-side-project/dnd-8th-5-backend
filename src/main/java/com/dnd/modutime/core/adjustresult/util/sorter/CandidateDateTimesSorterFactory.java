package com.dnd.modutime.core.adjustresult.util.sorter;

import com.dnd.modutime.core.adjustresult.application.CandidateDateTimeSortStandard;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// TODO: test
@Component
@RequiredArgsConstructor
public class CandidateDateTimesSorterFactory {

    private final Map<String, CandidateDateTimesSorter> sorters;

    public CandidateDateTimesSorter getInstance(CandidateDateTimeSortStandard candidateDateTimeSortStandard) {
        if (candidateDateTimeSortStandard.isFast()) {
            return sorters.get("fastFirstSorter");
        }
        if (candidateDateTimeSortStandard.isLong()) {
            return sorters.get("longFirstSorter");
        }
        throw new IllegalArgumentException("해당하는 정렬기가 없습니다.");
    }
}
