package com.dnd.modutime.core.adjustresult.application.response;

import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTimeParticipantName;
import com.dnd.modutime.core.participant.domain.Participants;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AdjustmentResultResponse {

    @JsonProperty(value = "candidateTimes")
    private List<CandidateDateTimeResponse> candidateDateTimeResponse;

    public static AdjustmentResultResponse from(List<CandidateDateTime> candidateDateTimes, Participants participants) {
        return new AdjustmentResultResponse(candidateDateTimes.stream()
                .map(it -> getCandidateDateTimeResponse(it, participants))
                .collect(Collectors.toList()));
    }

    private static CandidateDateTimeResponse getCandidateDateTimeResponse(CandidateDateTime candidateDateTime, Participants participants) {
        LocalTime startTime = candidateDateTime.getStartDateTime().toLocalTime();
        LocalTime endTime = candidateDateTime.getEndDateTime().toLocalTime();
        if (isZeroTime(startTime, endTime)) {
            startTime = null;
            endTime = null;
        }
        var availableParticipantNames = candidateDateTime.getParticipantNames().stream()
                .map(CandidateDateTimeParticipantName::getName)
                .collect(Collectors.toList());
        return new CandidateDateTimeResponse(
                candidateDateTime.getId(),
                candidateDateTime.getStartDateTime().toLocalDate(),
                candidateDateTime.getStartDateTime().getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                startTime,
                endTime,
                availableParticipantNames,
                participants.getExcludedParticipantNames(availableParticipantNames),
                candidateDateTime.isConfirmed());
    }

    private static boolean isZeroTime(LocalTime startTime, LocalTime endTime) {
        return startTime.equals(endTime) && startTime.equals(LocalTime.of(0, 0));
    }
}
