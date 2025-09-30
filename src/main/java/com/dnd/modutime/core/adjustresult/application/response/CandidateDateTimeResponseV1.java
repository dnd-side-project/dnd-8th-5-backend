package com.dnd.modutime.core.adjustresult.application.response;

import com.dnd.modutime.core.adjustresult.domain.CandidateDateTime;
import com.dnd.modutime.core.adjustresult.domain.CandidateDateTimeParticipantName;
import com.dnd.modutime.core.participant.domain.Participants;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
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
public class CandidateDateTimeResponseV1 {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate date;
    private String dayOfWeek;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
    private LocalTime endTime;
    private List<String> availableParticipantNames;
    private List<String> unavailableParticipantNames;

    public static CandidateDateTimeResponseV1 of(CandidateDateTime candidateDateTime, Participants participants) {
        final var response = new CandidateDateTimeResponseV1();
        var startTime = candidateDateTime.getStartDateTime().toLocalTime();
        var endTime = candidateDateTime.getEndDateTime().toLocalTime();
        if (isZeroTime(startTime, endTime)) {
            startTime = null;
            endTime = null;
        }
        response.id = candidateDateTime.getId();
        response.date = candidateDateTime.getStartDateTime()
                .toLocalDate();
        response.dayOfWeek = candidateDateTime.getStartDateTime()
                .getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        response.startTime = startTime;
        response.endTime = endTime;
        final List<String> availableParticipantNames = candidateDateTime.getParticipantNames().stream()
                .map(CandidateDateTimeParticipantName::getName)
                .collect(Collectors.toList());
        response.availableParticipantNames = availableParticipantNames;
        response.unavailableParticipantNames = participants.getExcludedParticipantNames(availableParticipantNames);
        return response;
    }

    private static boolean isZeroTime(LocalTime startTime, LocalTime endTime) {
        return startTime.equals(endTime) && startTime.equals(LocalTime.of(0, 0));
    }
}
