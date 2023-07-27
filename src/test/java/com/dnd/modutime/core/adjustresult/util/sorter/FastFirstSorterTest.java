package com.dnd.modutime.core.adjustresult.util.sorter;

import static com.dnd.modutime.fixture.TimeFixture._11_00;
import static com.dnd.modutime.fixture.TimeFixture._12_00;
import static com.dnd.modutime.fixture.TimeFixture._14_00;
import static com.dnd.modutime.fixture.TimeFixture._2023_02_08;
import static org.assertj.core.api.Assertions.assertThat;

import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTimeParticipantName;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class FastFirstSorterTest {

    private final CandidateDateTimesSorter candidateDateTimesSorter = new FastFirstSorter();

    @Test
    void 인원이_가장_많고_빠른시간_순으로_정렬된다() {
        List<CandidateDateTime> candidateDateTimes = new ArrayList<>();
        candidateDateTimes.add(getCandidateTime(LocalDateTime.of(_2023_02_08, _12_00), LocalDateTime.of(_2023_02_08, _14_00), List.of("수진", "동호", "주현")));
        candidateDateTimes.add(getCandidateTime(LocalDateTime.of(_2023_02_08, _11_00), LocalDateTime.of(_2023_02_08, _14_00), List.of("수진", "동호", "세희")));
        candidateDateTimes.add(getCandidateTime(LocalDateTime.of(_2023_02_08, _14_00), LocalDateTime.of(_2023_02_08, _14_00), List.of("수진")));
        candidateDateTimesSorter.sort(candidateDateTimes);
        assertThat(candidateDateTimes.stream()
                .map(CandidateDateTime::getStartDateTime)
                .collect(Collectors.toList()))
                .containsExactly(LocalDateTime.of(_2023_02_08, _11_00),
                        LocalDateTime.of(_2023_02_08, _12_00),
                        LocalDateTime.of(_2023_02_08, _14_00));
    }

    private CandidateDateTime getCandidateTime(LocalDateTime startTime, LocalDateTime endTime, List<String> names) {
        return new CandidateDateTime(null, startTime, endTime, false,
                names.stream()
                        .map(CandidateDateTimeParticipantName::new)
                        .collect(Collectors.toList()));
    }
}