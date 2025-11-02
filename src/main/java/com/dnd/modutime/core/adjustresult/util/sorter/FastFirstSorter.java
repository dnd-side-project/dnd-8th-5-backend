package com.dnd.modutime.core.adjustresult.util.sorter;

import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class FastFirstSorter implements CandidateDateTimesSorter{

    @Override
    public void sort(List<CandidateDateTime> candidateDateTimes) {
        candidateDateTimes.sort(getComparator());
    }

    @Override
    public Comparator<CandidateDateTime> getComparator() {
        return Comparator
                .comparing(CandidateDateTime::getParticipantSize, Comparator.reverseOrder())
                .thenComparing(CandidateDateTime::getStartDateTime);
    }
}
