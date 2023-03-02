package com.dnd.modutime.adjustresult.util.sorter;

import com.dnd.modutime.adjustresult.domain.CandidateDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

// TODO: test
@Component
public class FastFirstSorter implements CandidateDateTimesSorter{
    @Override
    public void sort(final List<CandidateDateTime> candidateDateTimes) {
        Comparator<CandidateDateTime> compare = Comparator
                .comparing(CandidateDateTime::getParticipantSize, Comparator.reverseOrder())
                .thenComparing(CandidateDateTime::getStartDateTime)
                .thenComparing(CandidateDateTime::calculateTerm);
        candidateDateTimes.sort(compare);
    }
}
