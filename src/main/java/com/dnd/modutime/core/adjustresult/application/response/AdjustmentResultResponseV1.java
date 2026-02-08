package com.dnd.modutime.core.adjustresult.application.response;

import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.participant.domain.Participants;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AdjustmentResultResponseV1 {

    @JsonProperty(value = "candidateTimes")
    private List<CandidateDateTimeResponseV1> candidateDateTimeResponse;

    public static AdjustmentResultResponseV1 of(List<CandidateDateTime> candidateDateTimes, Participants participants) {
        return new AdjustmentResultResponseV1(candidateDateTimes.stream()
                .map(it -> CandidateDateTimeResponseV1.of(it, participants))
                .collect(Collectors.toList()));
    }
}
