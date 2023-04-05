package com.dnd.modutime.core.adjustresult.application.response;

import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTimeParticipantName;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AdjustmentResultResponse {

    @JsonProperty(value = "candidateTimes")
    private List<CandidateDateTimeResponse> candidateDateTimeResponse;

    public static AdjustmentResultResponse from(List<CandidateDateTime> candidateDateTimes) {
        return new AdjustmentResultResponse(candidateDateTimes.stream()
                .map(candidateDateTime -> new CandidateDateTimeResponse(
                        candidateDateTime.getId(),
                        candidateDateTime.getStartDateTime().toLocalDate(),
                        candidateDateTime.getStartDateTime().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                        getTimeOrNull(candidateDateTime.getStartDateTime().toLocalTime()),
                        getTimeOrNull(candidateDateTime.getEndDateTime().toLocalTime()),
                        candidateDateTime.getParticipantNames().stream()
                                .map(CandidateDateTimeParticipantName::getName)
                                .collect(Collectors.toList()),
                        candidateDateTime.isConfirmed()))
                .collect(Collectors.toList()));
    }

    private static LocalTime getTimeOrNull(LocalTime time) {
        if (isZeroTime(time)) {
            return null;
        }
        return time;
    }

    private static boolean isZeroTime(LocalTime time) {
        return time.equals(LocalTime.of(0, 0));
    }
}
