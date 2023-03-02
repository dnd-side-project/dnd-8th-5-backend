package com.dnd.modutime.adjustresult.application;

import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CandidateDateTimeSorter {

    public void sort(List<CandidateDateTimeDto> candidateDateTimesDto) {
        if (sortedBy.equals(SortedBy.FAST)) {
            Comparator<CandidateDateTimeDto> compare = Comparator
                    .comparing(CandidateDateTimeDto::getStartDateTime)
                    .thenComparing(it -> ChronoUnit.SECONDS.between(it.getEndDateTime(), it.getStartDateTime()));
            candidateDateTimeDtos.stream()
                    .sorted(compare)
                    .collect(Collectors.toList());
        }
        if (sortedBy.equals(SortedBy.LONG)) {
            Comparator<CandidateDateTimeDto> compare = Comparator
                    .comparing(it -> ChronoUnit.SECONDS.between(it.getEndDateTime(), it.getStartDateTime()))
                    .thenComparing(CandidateDateTimeDto::getStartDateTime);
            candidateDateTimeDtos.stream()
                    .sorted(compare)
                    .collect(Collectors.toList());
        }
    }
}
